package com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

import java.util.Map;

public record IRAction(Identifier actionId, ScriptType returnType, Map<String, IRValue> arguments) implements IRNode {
}

