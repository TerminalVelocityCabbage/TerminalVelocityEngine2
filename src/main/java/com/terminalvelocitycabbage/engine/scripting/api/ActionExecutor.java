package com.terminalvelocitycabbage.engine.scripting.api;

@FunctionalInterface
public interface ActionExecutor {
    void execute(ActionContext context);
}

