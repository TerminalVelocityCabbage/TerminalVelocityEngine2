package com.terminalvelocitycabbage.engine.scheduler;

/**
 * A TaskContext is how you pass data between stages of a schedule task. It allows you to pass a return value as an
 * object on to subsequent tasks. This is especially useful for I/O operations. You can schedule an async task to
 * fetch some data and then return it to another blocking task once it's done reading.
 */
public class TaskContext {

    Task task;
    Object previousReturnValue;
    Object returnValue;

    public TaskContext(Task task) {
        this.task = task;
    }

    public TaskContext(Task task, Object previousReturnValue) {
        this.task = task;
        this.previousReturnValue = previousReturnValue;
    }

    /**
     * @return the {@link Task} which this TaskContext belongs to
     */
    public Task task() {
        return task;
    }

    /**
     * @return an arbitrary object that represents whatever the parent to this task set as it's return value.
     * This task should know how to cast it into usable data
     */
    public Object previousReturn() {
        return previousReturnValue;
    }

    /**
     * @param value An object which will be passed on to the children of this task (tasks defined in the
     * {@link TaskBuilder#then(Task)} node of the builder that built this task) within the child tasks' TaskContext.
     */
    public void setReturn(Object value) {
        this.returnValue = value;
    }

    /**
     * @return true if this task has a parent task.
     */
    public boolean hasPrevious() {
        return previousReturnValue != null;
    }
}
