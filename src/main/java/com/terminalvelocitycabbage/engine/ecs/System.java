package com.terminalvelocitycabbage.engine.ecs;

/**
 * A system that operates on any family of components. Should be extended into a useful system then added to your
 * {@link Manager} with {@link Manager#createSystem(Class)} to operate on the filtered entities which are retrieved
 * using an {@link ComponentFilter}.
 */
public abstract class System {

    protected System() { }

    /**
     * @param manager The ECS manager
     * @param deltaTime
     */
    public abstract void update(Manager manager, float deltaTime);
}
