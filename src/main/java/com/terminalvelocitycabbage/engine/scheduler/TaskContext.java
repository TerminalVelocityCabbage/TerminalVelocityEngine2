package com.terminalvelocitycabbage.engine.scheduler;

public class TaskContext {

    Task task;
    Object previousReturnValue;
    Object value;

    public TaskContext(Task task) {
        this.task = task;
    }

    public TaskContext(Task task, Object previousReturnValue) {
        this.task = task;
        this.previousReturnValue = previousReturnValue;
    }

    public Task task() {
        return task;
    }

    public Object previous() {
        return previousReturnValue;
    }

    public Object value() {
        return value;
    }

    public void setReturn(Object value) {
        this.value = value;
    }

    public boolean hasPrevious() {
        return previousReturnValue != null;
    }
}
