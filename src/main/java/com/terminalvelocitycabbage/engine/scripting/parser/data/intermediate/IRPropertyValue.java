package com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptProperty;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;

public record IRPropertyValue(ScriptProperty<?, ?> property) implements IRValue {

    @Override
    public ScriptType type() {
        return property.valueType();
    }

    @Override
    public String toString() {
        return "PROPERTY<" + type().getIdentifier() + ">(" + property.getIdentifier() + ")";
    }
}


