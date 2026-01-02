package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.parser.data.ScriptLine;
import com.terminalvelocitycabbage.engine.scripting.parser.data.SentenceNode;

import java.util.List;

public final class SentenceParser {

    public static SentenceNode parse(ScriptLine line) {

        String raw = line.text().trim();

        List<String> tokens =
                List.of(raw.split("\\s+"));

        return new SentenceNode(raw, tokens, line.lineNumber());
    }
}

