package com.terminalvelocitycabbage.engine.scripting.parser.data;

import java.util.List;

public final class PropertyValue implements ScriptValue {

    private final List<String> path; // game.name → ["game", "name"]

    public PropertyValue(List<String> path) {
        this.path = path;
    }
}

