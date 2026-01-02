package com.terminalvelocitycabbage.engine.scripting.parser.data;

import java.util.List;

//Represents a root level block of code in a script
public record ScriptBlock(
        String headerLine,
        List<ScriptLine> body,
        int headerLineNumber
) {}

