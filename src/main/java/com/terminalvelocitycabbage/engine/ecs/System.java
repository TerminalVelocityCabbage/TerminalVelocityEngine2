package com.terminalvelocitycabbage.engine.ecs;

import java.util.List;

/**
 * A system that operates on any family of components. Should be extended into a useful system then added to your
 * {@link Manager} with {@link Manager#createSystem(Class)} to operate on the filtered entities which are retrieved
 * using an {@link ComponentFilter}.
 */
public abstract class System {

    protected System() { }

    /**
     * @param entities The list of entities that were passed to this system filtered by the Repeater's filter
     * @param deltaTime
     */
    public abstract void update(List<Entity> entities, float deltaTime);
}
