package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.core.CoreLibrary;
import com.terminalvelocitycabbage.engine.scripting.core.CoreTypes;
import com.terminalvelocitycabbage.engine.scripting.parser.data.SentenceNode;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.*;

import java.util.List;

public final class SentenceToIRAction {

    public static IRAction parse(
            SentenceNode sentence,
            ParsingContext context
    ) {
        if (sentence.verb().equals("print")) {

            if (sentence.arguments().size() != 1) {
                throw new RuntimeException(
                        "print expects exactly 1 argument at line "
                                + sentence.lineNumber()
                );
            }

            String rawArg = sentence.arguments().get(0);

            IRValue value;

            if (rawArg.startsWith("\"")) {
                value = new IRLiteral(
                        CoreTypes.TEXT,
                        rawArg.substring(1, rawArg.length() - 1)
                );
            } else {
                // TODO property access for now
                value = new IRProperty(
                        CoreTypes.TEXT,
                        Identifier.of("test:game.name"),
                        rawArg
                );
            }

            return new IRAction(
                    new Identifier(CoreLibrary.CORE_NAMESPACE, "print"),
                    CoreTypes.VOID,
                    List.of(new IRArgument("value", value))
            );
        }

        throw new RuntimeException(
                "Unknown action '" + sentence.verb()
                        + "' at line " + sentence.lineNumber()
        );
    }
}

