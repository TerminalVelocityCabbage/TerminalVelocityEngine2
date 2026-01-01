package com.terminalvelocitycabbage.engine.scripting.api;

import com.terminalvelocitycabbage.engine.registry.Identifiable;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public record ScriptProperty<T> (
        Identifier identifier,
        ScriptType owner,
        ScriptType valueType,
        PropertyAccess<T> access,
        ScriptVisibility visibility,
        String documentation
) implements Identifiable {

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }
}

