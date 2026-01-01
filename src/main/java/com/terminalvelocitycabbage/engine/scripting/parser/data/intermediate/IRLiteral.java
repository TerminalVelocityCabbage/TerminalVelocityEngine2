package com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

public record IRLiteral(ScriptType type, Object value) implements IRValue {
}

