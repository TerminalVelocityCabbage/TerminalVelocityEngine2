package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptAction;

public final class CallActionInstruction implements ScriptInstruction {

    private final ScriptAction action;
    private final int[] argumentSlots;

    public CallActionInstruction(ScriptAction action, int[] argumentSlots) {
        this.action = action;
        this.argumentSlots = argumentSlots;
    }

    @Override
    public void execute(ExecutionContext context) {
        Object[] args = new Object[argumentSlots.length];
        for (int i = 0; i < argumentSlots.length; i++) {
            args[i] = context.getLocal(argumentSlots[i]);
        }
        action.invoke(args);
    }
}

