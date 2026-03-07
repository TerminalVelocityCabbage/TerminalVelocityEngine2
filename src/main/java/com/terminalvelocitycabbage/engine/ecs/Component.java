package com.terminalvelocitycabbage.engine.ecs;

import com.terminalvelocitycabbage.engine.pools.Poolable;

/**
 * A component is meant to be a way to store data that changes over time.
 */
public interface Component extends Poolable {

    //Allows parsing components from .entity.toml files
    default void parseComponentField(String field, String value) {}

    //Any data a user may want to store on that component.
    //This requires a 0 args constructor, so the component should have default values.
}
