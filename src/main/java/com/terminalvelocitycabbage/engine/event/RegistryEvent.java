package com.terminalvelocitycabbage.engine.event;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifiable;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.engine.registry.RegistryPair;

public class RegistryEvent<T> extends Event {

    Registry<T> registry;

    public RegistryEvent(Identifier name, Registry<T> registry) {
        super(name);
        this.registry = registry;
    }

    public RegistryPair<T> register(Identifier identifier, T item) {
        return registry.register(identifier, item);
    }

    public RegistryPair<T> register(T item) {
        if (item instanceof Identifiable) {
            return registry.register(item);
        } else {
            Log.crash("Cannot register item " + item.getClass().getName() + " since it does not implement Identifiable. Use method with explicit identifier instead.");
        }
        return null;
    }

    public Registry<T> getRegistry() {
        return registry;
    }
}
