package com.terminalvelocitycabbage.engine.scripting.api;

import com.terminalvelocitycabbage.engine.registry.Identifiable;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public record ScriptConstant(Identifier identifier, ScriptType type, Object value, String documentation) implements Identifiable {

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }
}
