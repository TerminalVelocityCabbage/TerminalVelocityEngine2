package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

import java.util.HashMap;
import java.util.Map;

public final class ExecutionContext {

    private final Map<ScriptType, Object> scopedValues = new HashMap<>();

    public void setScopeValue(ScriptType type, Object value) {
        scopedValues.put(type, value);
    }

    public Object getCurrentScopeValue(ScriptType type) {
        Object value = scopedValues.get(type);
        if (value == null) {
            throw new RuntimeException(
                    "No value in scope for type " + type.getIdentifier()
            );
        }
        return value;
    }
}



