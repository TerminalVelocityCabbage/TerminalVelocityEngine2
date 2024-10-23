package com.terminalvelocitycabbage.engine.scheduler;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class TaskBuilder {

    private Identifier identifier;
    private Consumer<TaskContext> consumer;
    private boolean repeat;
    private long repeatInterval;
    private boolean delay;
    private long delayInMillis;
    private boolean async;
    private final List<Task> subsequentTasks;

    private TaskBuilder() {
        repeat = false;
        delay = false;
        async = false;
        subsequentTasks = new ArrayList<>();
    }

    /**
     * Starts a new Task Builder for configuring a new task
     * @return A new instance of a TaskBuilder
     */
    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    /**
     * Assigns a unique identifier to this task from a namespace and task name, it then appends a uuid to the end
     * of the task name to make sure it is unique.
     * @param namespace The namespace of the identifier being created. Usually the ID of your game or mod
     * @param taskName The name which identifies this task
     * @return this TaskBuilder
     */
    public TaskBuilder identifier(String namespace, String taskName) {
        this.identifier = new Identifier(namespace, taskName + "[" + UUID.randomUUID() + "]");
        return this;
    }

    /**
     * Assigns the consumer which will be carried out by the scheduler when this task is executed.
     * @param taskConsumer a consumer that defines the action that this task is intended to complete
     * @return this TaskBuilder
     */
    public TaskBuilder executes(Consumer<TaskContext> taskConsumer) {
        consumer = taskConsumer;
        return this;
    }

    /**
     * Defines the repeat interval for this task if desired
     * @param interval the interval (as an integer) for which this task will be repeated.
     * @param timeUnit the time unit for the interval specified
     * @return this TaskBuilder
     */
    public TaskBuilder repeat(int interval, TimeUnit timeUnit) {
        repeat = true;
        repeatInterval = TimeUnit.MILLISECONDS.convert(interval, timeUnit);
        return this;
    }

    /**
     * Defines the time between when this task is added to the scheduler and when it should execute
     * @param interval the interval (as an integer) by which this task will be delayed by.
     * @param timeUnit the time unit for the interval specified
     * @return this TaskBuilder
     */
    public TaskBuilder delay(int interval, TimeUnit timeUnit) {
        delay = true;
        delayInMillis = TimeUnit.MILLISECONDS.convert(interval, timeUnit);
        return this;
    }

    /**
     * configures this task to be run async (non-blocking).
     * @return this TaskBuilder
     */
    public TaskBuilder async() {
        async = true;
        return this;
    }

    /**
     * Allows you to specify another task to be executed after this task, data can be passed between tasks by using the
     * TaskContext on a task accessible by the tasks' consumer. see {@link TaskContext} for more information.
     * @param task the task which will be scheduled after this task
     * @return this TaskBuilder
     */
    public TaskBuilder then(Task task) {
        return then(task, null);
    }

    /**
     * Allows you to specify another task to be executed after this task, data can be passed between tasks by using the
     * TaskContext on a task accessible by the tasks' consumer. see {@link TaskContext} for more information.
     * @param task the task which will be scheduled after this task
     * @param pool the pool this subsequent task will belong to
     * @return this TaskBuilder
     */
    public TaskBuilder then(Task task, Identifier pool) {
        if (task == null) return this;
        task.setPool(pool);
        subsequentTasks.add(task);
        return this;
    }

    /**
     * Builds this TaskBuilder config into a new Task.
     * A Task must have 2 things at least:
     *  - An Identifier
     *  - A Consumer
     *  All other nodes of this builder are optional.
     * @return A new Task based on the configuration done by this builder
     */
    public Task build() {

        if (identifier == null) {
            Log.crash("Tried to create a non-identifiable task",
                    "A task must have an identifier associated with it",
                    "make sure to call .identifier(...) on your TaskBuilder chain before calling .build()",
                    new IllegalStateException("tried to build task with no identifier"));
        }

        if (consumer == null) {
            Log.crash("Tried to create a taskless task",
                    "A task must have an .executes() node associated with it",
                    new IllegalStateException("tried to build task with no identifier"));
        }

        return new Task(identifier, consumer, repeat, repeatInterval, delay, delayInMillis, async, subsequentTasks);
    }
}
