package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.registry.*;

public record ParsingContext(
        ScriptActionRegistry actions,
        ScriptPropertyRegistry properties,
        ScriptEventRegistry events,
        ScriptTypeRegistry types) {

}

