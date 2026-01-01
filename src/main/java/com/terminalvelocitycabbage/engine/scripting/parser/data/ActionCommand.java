package com.terminalvelocitycabbage.engine.scripting.parser.data;

import com.terminalvelocitycabbage.engine.registry.Identifiable;

import java.util.Map;

public final class ActionCommand implements ScriptCommand {

    private final Identifiable actionIdentifier;
    private final Map<String, ScriptValue> arguments;

    public ActionCommand(Identifiable actionIdentifier, Map<String, ScriptValue> arguments) {
        this.actionIdentifier = actionIdentifier;
        this.arguments = arguments;
    }
}
