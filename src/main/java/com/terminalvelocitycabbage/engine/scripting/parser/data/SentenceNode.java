package com.terminalvelocitycabbage.engine.scripting.parser.data;

import java.util.List;

public record SentenceNode(String rawText, List<String> tokens, int lineNumber) {

}



