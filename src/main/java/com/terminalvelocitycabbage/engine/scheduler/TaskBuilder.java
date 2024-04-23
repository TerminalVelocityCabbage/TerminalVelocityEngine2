package com.terminalvelocitycabbage.engine.scheduler;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.ArrayList;
import java.util.List;
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

    public static TaskBuilder builder() {
        return new TaskBuilder();
    }

    public TaskBuilder identifier(Identifier identifier) {
        this.identifier = identifier;
        return this;
    }

    public TaskBuilder executes(Consumer<TaskContext> taskConsumer) {
        consumer = taskConsumer;
        return this;
    }

    public TaskBuilder repeat(int interval, TimeUnit timeUnit) {
        repeat = true;
        repeatInterval = TimeUnit.MILLISECONDS.convert(interval, timeUnit);
        return this;
    }

    public TaskBuilder delay(int interval, TimeUnit timeUnit) {
        delay = true;
        delayInMillis = TimeUnit.MILLISECONDS.convert(interval, timeUnit);
        return this;
    }

    public TaskBuilder async() {
        async = true;
        return this;
    }

    public TaskBuilder then(Task task) {
        subsequentTasks.add(task);
        return this;
    }

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
