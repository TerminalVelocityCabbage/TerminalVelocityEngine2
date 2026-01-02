package com.terminalvelocitycabbage.engine.scripting.api.syntax;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

public record SyntaxArgument(
        String name,
        ScriptType type
) implements SyntaxPart {}

