package com.terminalvelocitycabbage.engine.scripting.api;

@FunctionalInterface
public interface ScriptActionExecutor {
    void execute(Object[] arguments);
}

