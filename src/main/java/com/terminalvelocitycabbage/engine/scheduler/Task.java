package com.terminalvelocitycabbage.engine.scheduler;

import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.List;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;

public final class Task {

    private final StampedLock lock = new StampedLock();
    private boolean initialized;
    private final Identifier identifier;
    private final Consumer<TaskContext> consumer;
    private volatile boolean complete;
    private volatile boolean remove;
    private final boolean repeat;
    private final long repeatInterval; //In millis
    private long lastExecuteTimeMillis;
    private boolean delay;
    private final long delayTime; //In millis
    private long executeTime;
    private final boolean async;
    private volatile boolean running;
    private final TaskContext context;
    private final List<Task> subsequentTasks;

    public Task(Identifier identifier, Consumer<TaskContext> consumer, boolean repeat, long repeatInterval, boolean delay, long delayInMillis, boolean async, List<Task> subsequentTasks) {
        this.identifier = identifier;
        this.consumer = consumer;
        this.remove = false;
        this.repeat = repeat;
        this.repeatInterval = repeatInterval;
        this.delay = delay;
        this.delayTime = delayInMillis;
        this.async = async;
        this.running = false;
        this.context = new TaskContext(this);
        this.subsequentTasks = subsequentTasks;
    }

    //Used to set times for execute and resets this task to default status in case of re-use (not advised, but allowed)
    public Task init() {
        if (delay) executeTime = System.currentTimeMillis() + delayTime;
        if (repeat) lastExecuteTimeMillis = System.currentTimeMillis() - repeatInterval;

        long l = lock.writeLock();
        complete = false;
        remove = false;
        running = false;
        lock.unlockWrite(l);

        initialized = true;
        return this;
    }

    //Used to set times for execute and such with a previous return value in context
    public Task init(Object previousReturn) {
        this.context.previousReturnValue = previousReturn;
        return init();
    }

    public Identifier identifier() {
        return identifier;
    }

    public Consumer<TaskContext> consumer() {
        return consumer;
    }

    public Consumer<TaskContext> getAndMarkConsumerRunning() {
        long l = lock.writeLock();
        markRunning();
        lock.unlockWrite(l);
        return consumer;
    }

    public long lastExecuteTimeMillis() {
        return lastExecuteTimeMillis;
    }

    public void execute() {
        consumer().accept(this.context());
        lastExecuteTimeMillis = System.currentTimeMillis();
    }

    public boolean remove() {
        return remove;
    }

    public void markRemove() {
        long l = lock.writeLock();
        remove = true;
        running = false;
        lock.unlockWrite(l);
    }

    public boolean repeat() {
        return repeat;
    }

    public long repeatInterval() {
        return repeatInterval;
    }

    public boolean delay() {
        return delay;
    }

    public long executeTime() {
        return executeTime;
    }

    public boolean initialized() {
        return initialized;
    }

    public boolean async() {
        return async;
    }

    public void markRunning() {
        this.running = true;
    }

    public boolean running() {
        return running;
    }

    public TaskContext context() {
        return this.context;
    }

    public boolean complete() {
        return complete;
    }

    public boolean hasSubsequentTasks() {
        return !subsequentTasks.isEmpty();
    }

    public List<Task> subsequentTasks() {
        return subsequentTasks;
    }

    public StampedLock getLock() {
        return lock;
    }

    public void markComplete() {
        long l = lock.writeLock();
        complete = true;
        lock.unlockWrite(l);
    }
}
