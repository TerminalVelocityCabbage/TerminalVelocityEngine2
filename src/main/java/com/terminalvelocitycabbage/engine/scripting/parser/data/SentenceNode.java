package com.terminalvelocitycabbage.engine.scripting.parser.data;

import java.util.List;

public record SentenceNode(
        String verb,
        List<String> arguments,
        int lineNumber
) {}

