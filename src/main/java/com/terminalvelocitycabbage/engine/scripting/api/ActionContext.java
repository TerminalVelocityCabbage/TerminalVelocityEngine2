package com.terminalvelocitycabbage.engine.scripting.api;

import java.util.Map;

public record ActionContext(Map<String, Object> arguments) {

    @SuppressWarnings("unchecked")
    public <T> T get(String name) {
        return (T) arguments.get(name);
    }
}
