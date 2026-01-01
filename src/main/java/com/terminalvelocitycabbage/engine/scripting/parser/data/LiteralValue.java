package com.terminalvelocitycabbage.engine.scripting.parser.data;

public final class LiteralValue implements ScriptValue {

    private final Object value; // string, number, boolean

    public LiteralValue(Object value) {
        this.value = value;
    }
}

