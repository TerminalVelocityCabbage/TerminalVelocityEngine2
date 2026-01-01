package com.terminalvelocitycabbage.engine.scripting.api.syntax;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

import java.util.Set;

public final class Syntax {

    private Syntax() {}

    public static LiteralElement literal(String... literals) {
        return new LiteralElement(Set.of(literals));
    }

    public static ArgumentElement argument(String name, ScriptType type) {
        return new ArgumentElement(name, type);
    }

}
