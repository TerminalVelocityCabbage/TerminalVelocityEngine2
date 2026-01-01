package com.terminalvelocitycabbage.engine.scripting.api;

import com.terminalvelocitycabbage.engine.registry.Identifier;

public record ScriptConstant(Identifier id, ScriptType type, Object value, String documentation) {

}
