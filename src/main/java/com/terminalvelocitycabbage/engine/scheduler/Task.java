package com.terminalvelocitycabbage.engine.scheduler;

import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.List;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;

public final class Task {

    private final StampedLock lock = new StampedLock();
    private boolean initialized;
    private final Identifier identifier;
    private Identifier pool;
    private final Consumer<TaskContext> consumer;
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
        this.pool = null;
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

    /**
     * Initializes this task by setting (if applicable to this task) it's future execute time based on the delay value
     * the future execute time if this task is intended to be a repeating task & finally in case this task is being
     * re-used from a previous run (can be allowed if the task is not currently queued) we reset the status of the
     * task (completeness, removal status, and running status) to default..
     * @return this task
     */
    public Task init() {
        if (delay) executeTime = System.currentTimeMillis() + delayTime;
        if (repeat) lastExecuteTimeMillis = System.currentTimeMillis() - repeatInterval;

        long l = lock.writeLock();
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

    /**
     * Marks this task as running and returns the consumer
     * @return This task's consumer
     */
    public Consumer<TaskContext> getAndMarkConsumerRunning() {
        long l = lock.writeLock();
        markRunning();
        lock.unlockWrite(l);
        return consumer();
    }

    public long lastExecuteTimeMillis() {
        return lastExecuteTimeMillis;
    }

    /**
     * Executes this task's consumer
     */
    public void execute() {
        consumer().accept(this.context());
        lastExecuteTimeMillis = System.currentTimeMillis();
    }

    public boolean isSlatedToBeRemoved() {
        return remove;
    }

    /**
     * Marks this task as "to-be-removed"
     */
    public void markRemove() {
        long l = lock.writeLock();
        remove = true;
        running = false;
        lock.unlockWrite(l);
    }

    public boolean repeats() {
        return repeat;
    }

    public long repeatInterval() {
        return repeatInterval;
    }

    public boolean isDelayed() {
        return delay;
    }

    public long executeTime() {
        return executeTime;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isAsynchronous() {
        return async;
    }

    /**
     * Marks this task as running
     */
    public void markRunning() {
        this.running = true;
    }

    public boolean isRunning() {
        return running;
    }

    public TaskContext context() {
        return this.context;
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

    void setPool(Identifier pool) {
        this.pool = pool;
    }

    public Identifier pool() {
        return pool;
    }
}
