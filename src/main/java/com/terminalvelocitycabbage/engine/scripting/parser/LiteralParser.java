package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;
import com.terminalvelocitycabbage.engine.scripting.core.CoreTypes;

public final class LiteralParser {

    public static Object parse(String token, ScriptType type) {

        if (type.equals(CoreTypes.TEXT)) {
            return token.replace("\"", "");
        }

        if (type.equals(CoreTypes.NUMBER)) {
            return Double.parseDouble(token);
        }

        throw new RuntimeException(
                "Cannot parse literal '" + token + "' as " + type.getIdentifier()
        );
    }
}

