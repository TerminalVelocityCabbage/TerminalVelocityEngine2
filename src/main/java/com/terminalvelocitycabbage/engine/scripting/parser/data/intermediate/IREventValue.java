package com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

public record IREventValue(
        ScriptType type,
        String name
) implements IRValue {
}

