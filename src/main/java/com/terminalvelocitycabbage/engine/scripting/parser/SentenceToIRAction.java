package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptAction;
import com.terminalvelocitycabbage.engine.scripting.api.syntax.SyntaxPattern;
import com.terminalvelocitycabbage.engine.scripting.parser.data.SentenceNode;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRAction;

import java.util.ArrayList;
import java.util.List;

public final class SentenceToIRAction {

    public static IRAction parse(
            SentenceNode sentence,
            ParsingContext context
    ) {
        List<IRAction> matches = new ArrayList<>();

        for (ScriptAction action : context.actions().getRegistryContents().values()) {
            for (SyntaxPattern pattern : action.patterns()) {

                SyntaxMatcher.match(pattern, sentence, context)
                        .ifPresent(match -> {
                            matches.add(
                                    new IRAction(
                                            action.id(),
                                            action.returnType(),
                                            match.arguments()
                                    )
                            );
                        });
            }
        }

        if (matches.isEmpty()) {
            throw new RuntimeException(
                    "No matching action for: " + sentence.rawText()
            );
        }

        if (matches.size() > 1) {
            throw new RuntimeException(
                    "Ambiguous action for: " + sentence.rawText()
            );
        }

        return matches.get(0);
    }
}



