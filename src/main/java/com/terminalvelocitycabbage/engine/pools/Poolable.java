package com.terminalvelocitycabbage.engine.pools;

/**
 * An interface that defines an object which can be pooled
 */
public interface Poolable {
    //Resets the current pooled object to its default values
    public void setDefaults();

    //Optional reset method for if a component requires any cleanup
    default void cleanup() {}
}