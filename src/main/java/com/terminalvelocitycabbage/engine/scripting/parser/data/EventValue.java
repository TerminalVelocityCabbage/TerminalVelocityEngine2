package com.terminalvelocitycabbage.engine.scripting.parser.data;

public final class EventValue implements ScriptValue {

    private final String name; // "game"
    //TODO convert to identifier and guess what the namespace is if not explicit


    public EventValue(String name) {
        this.name = name;
    }
}

