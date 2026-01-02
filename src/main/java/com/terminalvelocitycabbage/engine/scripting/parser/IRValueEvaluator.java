package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptProperty;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRLiteralValue;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRPropertyValue;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRValue;

public final class IRValueEvaluator {

    public static Object evaluate(
            IRValue value,
            ExecutionContext context
    ) {
        if (value instanceof IRLiteralValue literal) {
            return literal.value();
        }

        if (value instanceof IRPropertyValue propertyValue) {
            return evaluateProperty(propertyValue, context);
        }

        throw new IllegalStateException(
                "Unknown IRValue: " + value.getClass()
        );
    }

    private static Object evaluateProperty(
            IRPropertyValue propertyValue,
            ExecutionContext context
    ) {
        ScriptProperty<?, ?> property = propertyValue.property();

        Object base = context.getCurrentScopeValue(property.ownerType());

        return property.access().get(base);
    }
}

