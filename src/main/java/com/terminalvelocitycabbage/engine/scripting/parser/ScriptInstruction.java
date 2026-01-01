package com.terminalvelocitycabbage.engine.scripting.parser;

public sealed interface ScriptInstruction permits LoadEventValueInstruction, LoadPropertyInstruction, CallActionInstruction {

    void execute(ExecutionContext context);
}

