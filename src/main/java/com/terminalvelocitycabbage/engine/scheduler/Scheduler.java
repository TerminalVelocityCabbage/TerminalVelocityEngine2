package com.terminalvelocitycabbage.engine.scheduler;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class Scheduler {

    //Stores all tasks that need to be added to the taskList.
    //Allow tasks to be scheduled by other tasks without concurrent modification exceptions.
    private final List<Task> taskQueue;
    //The current task list that is being executed
    private final List<Task> taskList;
    //The list of tasks that need to be removed, empty expect turing task processing
    private final List<Task> toRemove;

    public Scheduler() {
        taskList = new ArrayList<>();
        toRemove = new ArrayList<>();
        taskQueue = new ArrayList<>();
    }

    /**
     * To be called every game tick, processes all the task queues and processes all tasks.
     */
    public void tick() {

        //Add tasks scheduled for execution last tick and reset the queue for this tick
        taskList.addAll(taskQueue);
        taskQueue.clear();

        //Those tasks marked for removal with subsequent tasks need those to be scheduled for this run
        taskList.forEach(task -> {
            long l = task.getLock().readLock();
            if (task.remove()) {
                if (task.hasSubsequentTasks()) {
                    task.subsequentTasks().forEach((task1) -> scheduleTask(task1, task.context().value()));
                }
                toRemove.add(task);
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
            if (task.running() || task.remove()) {
                task.getLock().unlockRead(l);
                return;
            }

            task.getLock().unlockRead(l);
            //Check that the tasks here are initialized and error if not
            if (!task.initialized()) Log.crash("Task not initialized error", new IllegalStateException("Schedulers can only execute initialized tasks"));
            //Skip this task if it's delayed and not time to execute yet
            if (task.delay() && task.executeTime() > System.currentTimeMillis()) return;
            //Run the consumer
            if (task.repeat()) {
                if (System.currentTimeMillis() - task.lastExecuteTimeMillis() >= task.repeatInterval()) task.execute();
            } else {
                if (task.async()) {
                    CompletableFuture.supplyAsync(task::context)
                            .thenAcceptAsync(task.getAndMarkConsumerRunning())
                            .thenRunAsync(task::markRemove);
                } else {
                    task.execute();
                }
            }
            //If this is not a task marked at an interval we need to not run it next time, so mark for removal
            if (!task.repeat() && !task.running()) task.markRemove();
        });

        taskQueue.forEach(task -> Log.info(task.identifier().toString()));
    }

    /**
     * Schedules a given task for execution upon this scheduler's tick method being called if the conditions
     * for execution are met by the executor
     *
     * @param task the task to be added to the scheduler
     */
    public void scheduleTask(Task task) {
        if (getTask(task.identifier()).isPresent()) {
            Log.error("Tried to schedule task of same identifier: " + task.identifier().toString());
            return;
        }
        taskQueue.add(task.init());
    }

    /**
     * Schedules a given task for execution upon this scheduler's tick method being called if the conditions
     * for execution are met by the executor
     *
     * @param task the task to be added to the scheduler
     * @param previousReturn the return value of the previous run task
     */
    private void scheduleTask(Task task, Object previousReturn) {
        if (getTask(task.identifier()).isPresent()) {
            Log.error("Tried to schedule task of same identifier: " + task.identifier().toString());
            return;
        }
        taskQueue.add(task.init(previousReturn));
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
