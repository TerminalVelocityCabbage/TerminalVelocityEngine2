package com.terminalvelocitycabbage.engine.ecs;

import java.util.List;

/**
 * A system that operates on any family of components. Should be extended into a useful system then added to your
 * {@link Manager} with {@link Manager#createSystem(Class)} to operate on the filtered entities which are retrieved in
 * the user specified {@link System#getEntities()} method using an {@link ComponentFilter}.
 */
public abstract class System implements Comparable<System> {

    //The manager of this system
    private Manager manager;
    //The priority of this system, lower numbers are executed first
    private int priority;
    //Whether this system is currently processing
    private boolean processing;

    protected System() { }

    /**
     * @return a list of entities that match the requirements of this entity
     */
    public abstract List<Entity> getEntities();

    /**
     * @param deltaTime the amount of time in milliseconds passed since the last update
     */
    public abstract void update(float deltaTime);

    /**
     * @return the priority of this system
     */
    public int getPriority() {
        return priority;
    }

    /**
     * @param priority an int to replace this systems current priority
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * @return a boolean representing whether this system is currently being processed
     */
    public boolean isProcessing() {
        return processing;
    }

    /**
     * @param processing a boolean to represent the new current state of this system
     */
    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public Manager getManager() {
        return manager;
    }

    protected void setManager(Manager manager) {
        this.manager = manager;
    }

    @Override
    public int compareTo(System system) {
        return this.priority - system.priority;
    }
}
