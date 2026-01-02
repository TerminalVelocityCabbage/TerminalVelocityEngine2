package com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

public record IRLiteralValue(ScriptType type, Object value) implements IRValue {
}

