package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptProperty;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRLiteralValue;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRPropertyValue;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRValue;

import java.util.Optional;

public final class IRValueResolver {

    public static IRValue resolve(
            String token,
            ScriptType expectedType,
            ParsingContext context
    ) {
        // property?
        Optional<ScriptProperty<?, ?>> property = Optional.ofNullable(context.properties().resolve(token));

        if (property.isPresent()) {
            return new IRPropertyValue(property.get());
        }

        // literal
        Object literal =
                LiteralParser.parse(token, expectedType);

        return new IRLiteralValue(expectedType, literal);
    }
}

