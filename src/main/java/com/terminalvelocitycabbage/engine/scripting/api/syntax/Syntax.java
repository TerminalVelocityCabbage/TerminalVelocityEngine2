package com.terminalvelocitycabbage.engine.scripting.api.syntax;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

public final class Syntax {

    private Syntax() {}

    public static SyntaxLiteral literal(String literal) {
        return new SyntaxLiteral(literal);
    }

    public static SyntaxArgument argument(String name, ScriptType type) {
        return new SyntaxArgument(name, type);
    }

}
