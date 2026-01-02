package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.parser.data.ScriptLine;
import com.terminalvelocitycabbage.engine.scripting.parser.data.SentenceNode;

import java.util.ArrayList;
import java.util.List;

public final class SentenceParser {

    public static SentenceNode parse(ScriptLine line) {
        List<String> tokens = tokenize(line.text());

        if (tokens.isEmpty()) {
            throw new RuntimeException("Empty sentence at line " + line.lineNumber());
        }

        String verb = tokens.get(0);
        List<String> args = tokens.subList(1, tokens.size());

        return new SentenceNode(verb, args, line.lineNumber());
    }

    private static List<String> tokenize(String text) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
                current.append(c);
            } else if (c == ' ' && !inQuotes) {
                if (!current.isEmpty()) {
                    tokens.add(current.toString());
                    current.setLength(0);
                }
            } else {
                current.append(c);
            }
        }

        if (!current.isEmpty()) {
            tokens.add(current.toString());
        }

        if (inQuotes) {
            throw new RuntimeException("Unterminated string literal: " + text);
        }

        return tokens;
    }
}

