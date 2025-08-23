package com.terminalvelocitycabbage.engine.scheduler;

public class ScheduledTaskHandle implements TaskHandle {

    private volatile boolean cancelled = false;
    private volatile boolean done = false;

    private final ScheduledTask task;

    ScheduledTaskHandle(ScheduledTask task) {
        this.task = task;
    }

    @Override
    public void cancel() {
        cancelled = true;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public boolean isDone() {
        return done;
    }

    void setDone() {
        done = true;
    }
}
