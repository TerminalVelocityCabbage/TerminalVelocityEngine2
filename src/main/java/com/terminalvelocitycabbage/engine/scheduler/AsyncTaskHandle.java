package com.terminalvelocitycabbage.engine.scheduler;

import java.util.concurrent.Future;

public class AsyncTaskHandle<T> implements TaskHandle {
    private Future<?> future;
    private volatile boolean cancelled = false;
    private volatile boolean done = false;

    AsyncTaskHandle(Future<?> future) {
        this.future = future;
    }

    void setFuture(Future<?> future) {
        this.future = future;
    }

    void markDone() {
        this.done = true;
    }

    @Override
    public void cancel() {
        cancelled = true;
        if (future != null) {
            future.cancel(true);
        }
    }

    @Override
    public boolean isCancelled() {
        return cancelled || (future != null && future.isCancelled());
    }

    @Override
    public boolean isDone() {
        return done || (future != null && future.isDone());
    }
}
