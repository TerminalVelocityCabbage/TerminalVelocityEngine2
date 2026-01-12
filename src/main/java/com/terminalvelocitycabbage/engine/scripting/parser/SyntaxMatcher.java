package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.syntax.SyntaxArgument;
import com.terminalvelocitycabbage.engine.scripting.api.syntax.SyntaxLiteral;
import com.terminalvelocitycabbage.engine.scripting.api.syntax.SyntaxPart;
import com.terminalvelocitycabbage.engine.scripting.api.syntax.SyntaxPattern;
import com.terminalvelocitycabbage.engine.scripting.parser.data.SentenceNode;
import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class SyntaxMatcher {

    public static Optional<MatchedSyntax> match(
            SyntaxPattern pattern,
            SentenceNode sentence,
            ParsingContext context
    ) {
        List<String> tokens = sentence.tokens();
        List<SyntaxPart> parts = pattern.parts();

        if (tokens.size() != parts.size()) {
            return Optional.empty();
        }

        Map<String, IRValue> arguments = new HashMap<>();

        for (int i = 0; i < parts.size(); i++) {

            SyntaxPart part = parts.get(i);
            String token = tokens.get(i);

            if (part instanceof SyntaxLiteral literal) {
                if (!literal.text().equalsIgnoreCase(token)) {
                    return Optional.empty();
                }
            }

            if (part instanceof SyntaxArgument arg) {
                IRValue value =
                        IRValueResolver.resolve(token, arg.type(), context);
                arguments.put(arg.name(), value);
            }
        }

        return Optional.of(new MatchedSyntax(arguments));
    }
}


