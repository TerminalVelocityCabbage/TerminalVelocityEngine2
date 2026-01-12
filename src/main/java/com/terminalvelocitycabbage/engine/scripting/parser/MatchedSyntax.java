package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.parser.data.intermediate.IRValue;

import java.util.Map;

public record MatchedSyntax(Map<String, IRValue> arguments) {
}

