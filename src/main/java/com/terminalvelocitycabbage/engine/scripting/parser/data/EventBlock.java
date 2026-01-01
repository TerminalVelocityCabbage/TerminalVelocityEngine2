package com.terminalvelocitycabbage.engine.scripting.parser.data;

import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.List;

public final class EventBlock implements ScriptBlock {

    private final Identifier eventIdentifier;
    private final List<ScriptCommand> commands;

    public EventBlock(Identifier eventIdentifier, List<ScriptCommand> commands) {
        this.eventIdentifier = eventIdentifier;
        this.commands = commands;
    }
}
