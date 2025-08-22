package com.terminalvelocitycabbage.engine.scheduler;

public interface TaskHandle {
    void cancel();
    boolean isCancelled();
    boolean isDone();
}
