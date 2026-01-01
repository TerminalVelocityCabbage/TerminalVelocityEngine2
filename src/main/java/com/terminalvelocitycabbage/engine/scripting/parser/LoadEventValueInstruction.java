package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptEventValue;

public final class LoadEventValueInstruction implements ScriptInstruction {

    private final int targetSlot;
    private final ScriptEventValue value;

    public LoadEventValueInstruction(int targetSlot, ScriptEventValue value) {
        this.targetSlot = targetSlot;
        this.value = value;
    }

    @Override
    public void execute(ExecutionContext context) {
        Object event = context.getLocal(0); // slot 0 = event instance
        Object extracted = value.extract(event);
        context.setLocal(targetSlot, extracted);
    }
}

