package com.terminalvelocitycabbage.engine.scripting.api;

import com.terminalvelocitycabbage.engine.scripting.parser.ExecutionContext;

import java.util.Map;

public final class ActionContext {

    private final Map<String, Object> arguments;
    private final ExecutionContext executionContext;

    public ActionContext(
            Map<String, Object> arguments,
            ExecutionContext executionContext
    ) {
        this.arguments = arguments;
        this.executionContext = executionContext;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) arguments.get(name);
    }

    public ExecutionContext execution() {
        return executionContext;
    }
}


