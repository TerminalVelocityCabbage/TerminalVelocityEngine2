package com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

public record IRAction(Identifier actionId, ScriptType returnType, List<IRArgument> arguments) implements IRNode {
}

