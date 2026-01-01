package com.terminalvelocitycabbage.engine.registry;

import com.terminalvelocitycabbage.engine.util.touples.Pair;

/**
 * Represents a result from a registration used for doing things with data as it's being registered
 * @param <T> The type of object that this pair was registered under
 */
public class RegistryPair<T> extends Pair<Identifier, T> {

    /**
     * @param id the registration's identifier
     * @param value the registration's value
     */
    public RegistryPair(Identifier id, T value) {
        super(id, value);
    }

    /**
     * @return The identifier that was used to register this registration
     */
    public Identifier getIdentifier() {
        return getValue0();
    }

    /**
     * @return The value associated with this registration
     */
    public T getElement() {
        return getValue1();
    }
}
