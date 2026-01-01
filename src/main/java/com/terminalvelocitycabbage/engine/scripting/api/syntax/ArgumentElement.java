package com.terminalvelocitycabbage.engine.scripting.api.syntax;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

public record ArgumentElement(String name, ScriptType type) implements SyntaxElement {

}

