package com.terminalvelocitycabbage.engine.scripting.api;

import com.terminalvelocitycabbage.engine.registry.Identifiable;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public record ScriptProperty<O, V>(
        Identifier identifier,
        ScriptType ownerType,
        ScriptType valueType,
        PropertyAccess<O, V> access,
        ScriptVisibility visibility,
        String documentation
) implements Identifiable {

    @SuppressWarnings("unchecked")
    public Object get(Object instance) {
        return access.get((O) instance);
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }
}


