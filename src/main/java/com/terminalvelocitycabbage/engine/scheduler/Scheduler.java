package com.terminalvelocitycabbage.engine.scheduler;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.touples.Pair;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class Scheduler {

    //Stores all tasks that need to be added to the taskList.
    //Allow tasks to be scheduled by other tasks without concurrent modification exceptions.
    private final List<Task> taskQueue;
    //The current task list that is being executed
    private final List<Task> taskList;
    //The list of tasks that need to be removed, empty expect turing task processing
    private final List<Task> toRemove;
    //A map of all pools, and a pair representing the max number of tasks in this pool and the current number of tasks in that pool
    private final Map<Identifier, Pair<Integer, Integer>> poolCounts;

    public Scheduler() {
        taskList = new ArrayList<>();
        toRemove = new ArrayList<>();
        taskQueue = new ArrayList<>();
        poolCounts = new HashMap<>();
    }

    /**
     * Creates a pool with a limited number of tasks
     *
     * @param poolName The name of this pool represented by an identifier
     * @param maxTasks The maximum number of tasks that can be scheduled in this pool
     */
    public void createPool(Identifier poolName, int maxTasks) {
        poolCounts.put(poolName, new Pair<>(maxTasks, 0));
    }

    /**
     * @param poolName The name of this pool represented by an identifier
     * @return the number of tasks in this pool
     */
    public int numTasksInPool(Identifier poolName) {
        return poolCounts.get(poolName).getValue1();
    }

    /**
     * @param poolName The name of this pool represented by an identifier
     * @return the number of tasks allowed in this pool
     */
    public int poolMaxTasks(Identifier poolName) {
        return poolCounts.get(poolName).getValue0();
    }

    /**
     * @param poolName The name of this pool represented by an identifier
     * @return whether this pool has room
     */
    public boolean poolHasRoom(Identifier poolName) {
        var pool = poolCounts.get(poolName);
        return pool.getValue1() < pool.getValue0();
    }

    public void incrementPool(Identifier poolName) {
        var pool = poolCounts.get(poolName);
        var currentVal = pool.getValue1();
        pool.setValue1(currentVal + 1);
    }

    public void decrementPool(Identifier poolName) {
        var pool = poolCounts.get(poolName);
        var currentVal = pool.getValue1();
        pool.setValue1(currentVal - 1);
    }

    /**
     * To be called every game tick, processes all the task queues and processes all tasks.
     */
    public void update() {

        //Add tasks scheduled for execution last tick and reset the queue for this tick
        taskList.addAll(taskQueue);
        taskQueue.clear();

        //Those tasks marked for removal with subsequent tasks need those to be scheduled for this run
        taskList.forEach(task -> {
            long l = task.getLock().readLock();
            if (task.isSlatedToBeRemoved()) {
                if (task.hasSubsequentTasks()) {
                    task.subsequentTasks().forEach((task1) -> scheduleTask(task1, task1.pool(), task.context().returnValue(), true));
                }
                toRemove.add(task);
                if (task.pool() != null) decrementPool(task.pool());
            }
            task.getLock().unlockRead(l);
        });

        //Remove tasks that need to be removed and reset list for next tick
        taskList.removeAll(toRemove);
        toRemove.clear();

        //Process all the tasks
        taskList.forEach(task -> {
            //Some tasks like async tasks might get called more than once if we don't track their status
            long l = task.getLock().readLock();
            if (task.isRunning() || task.isSlatedToBeRemoved()) {
                task.getLock().unlockRead(l);
                return;
            }

            task.getLock().unlockRead(l);
            //Check that the tasks here are initialized and error if not
            if (!task.isInitialized()) Log.crash("Task not initialized error", new IllegalStateException("Schedulers can only execute initialized tasks"));
            //Skip this task if it's delayed and not time to execute yet
            if (task.isDelayed() && task.executeTime() > System.currentTimeMillis()) return;
            //Run the consumer
            if (task.repeats()) {
                if (System.currentTimeMillis() - task.lastExecuteTimeMillis() >= task.repeatInterval()) task.execute();
            } else {
                if (task.isAsynchronous()) {
                    CompletableFuture.supplyAsync(task::context)
                            .thenAcceptAsync(task.getAndMarkConsumerRunning())
                            .thenRunAsync(task::markRemove);
                } else {
                    task.execute();
                }
            }
            //If this is not a task marked at an interval we need to not run it next time, so mark for removal
            if (!task.repeats() && !task.isRunning()) task.markRemove();
        });
    }

    /**
     * Schedules a given task for execution upon this scheduler's tick method being called if the conditions
     * for execution are met by the executor
     *
     * @param task the task to be added to the scheduler
     */
    public void scheduleTask(Task task) {
        scheduleTask(task, null, null);
    }

    /**
     * Schedules a given task for execution upon this scheduler's tick method being called if the conditions
     * for execution are met by the executor
     *
     * @param task the task to be added to the scheduler
     * @param poolName what pool this task belongs to
     * @return if this task was successfully scheduled
     */
    public boolean scheduleTask(Task task, Identifier poolName) {
        return scheduleTask(task, poolName, null);
    }

    /**
     * Schedules a given task for execution upon this scheduler's tick method being called if the conditions
     * for execution are met by the executor
     *
     * @param task the task to be added to the scheduler
     * @param previousReturn the return value of the previous run task
     */
    private void scheduleTask(Task task, Object previousReturn) {
        scheduleTask(task, null, previousReturn);
    }

    /**
     * Schedules a given task for execution upon this scheduler's tick method being called if the conditions
     * for execution are met by the executor
     *
     * @param task the task to be added to the scheduler
     * @param poolName what pool this task belongs to
     * @param previousReturn the return value of the previous run task
     * @return whether this task was successfully scheduled or not
     */
    private boolean scheduleTask(Task task, Identifier poolName, Object previousReturn) {
        return scheduleTask(task, poolName, previousReturn, false);
    }

    /**
     * Schedules a given task for execution upon this scheduler's tick method being called if the conditions
     * for execution are met by the executor
     *
     * @param task the task to be added to the scheduler
     * @param poolName what pool this task belongs to
     * @param previousReturn the return value of the previous run task
     * @param ignoreFullPools whether to add this task to the queue when the specified pool is full or not
     * @return whether this task was successfully scheduled or not
     */
    private boolean scheduleTask(Task task, Identifier poolName, Object previousReturn, boolean ignoreFullPools) {
        if (getTask(task.identifier()).isPresent()) {
            Log.error("Tried to schedule task of same identifier: " + task.identifier().toString());
            return false;
        }

        if (poolName != null) {
            if (!poolHasRoom(poolName) && !ignoreFullPools) return false;
            task.setPool(poolName);
            incrementPool(poolName);
        }

        //TODO replace adding the task to a list with a map of identifiers to tasks so that task templates can be used and not overwritten by tasks using the same object
        if (previousReturn == null) {
            taskQueue.add(task.init());
        } else {
            taskQueue.add(task.init(previousReturn));
        }

        return true;
    }

    /**
     * Gets a task by Identifier if it exists on this scheduler
     *
     * @param identifier The identifier of the task which you hope to get
     * @return an Optional of type Task object scheduled in this scheduler with a matching identifier
     */
    public Optional<Task> getTask(Identifier identifier) {
        for (Task task : taskList) {
            if (task.identifier().equals(identifier)) {
                return Optional.of(task);
            }
        }
        for (Task task : taskQueue) {
            if (task.identifier().equals(identifier)) {
                return Optional.of(task);
            }
        }
        return Optional.empty();
    }

}
