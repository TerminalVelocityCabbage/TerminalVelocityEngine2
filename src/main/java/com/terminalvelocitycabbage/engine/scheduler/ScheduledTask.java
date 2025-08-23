package com.terminalvelocitycabbage.engine.scheduler;

class ScheduledTask {

    Runnable runnable;
    long nextRun;
    long interval;
    ScheduledTaskHandle handle;

    ScheduledTask(Runnable runnable, long nextRun, long interval) {
        this.runnable = runnable;
        this.nextRun = nextRun;
        this.interval = interval;
    }

    public ScheduledTaskHandle handle() {
        return handle;
    }
}

