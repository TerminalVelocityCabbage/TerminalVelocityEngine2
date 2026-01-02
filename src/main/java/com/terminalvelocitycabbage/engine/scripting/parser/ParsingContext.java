package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.registry.ScriptEventRegistry;
import com.terminalvelocitycabbage.engine.scripting.api.registry.ScriptTypeRegistry;

public final class ParsingContext {

    private final ScriptEventRegistry eventRegistry;
    private final ScriptTypeRegistry typeRegistry;

    public ParsingContext(
            ScriptEventRegistry eventRegistry,
            ScriptTypeRegistry typeRegistry
    ) {
        this.eventRegistry = eventRegistry;
        this.typeRegistry = typeRegistry;
    }

    public ScriptEventRegistry events() {
        return eventRegistry;
    }

    public ScriptTypeRegistry types() {
        return typeRegistry;
    }
}

