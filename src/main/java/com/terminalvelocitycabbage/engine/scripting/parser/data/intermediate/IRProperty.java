package com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

public record IRProperty(
        ScriptType type,
        Identifier propertyId,
        String accessPath // ex. "game.name"
) implements IRValue {
}
