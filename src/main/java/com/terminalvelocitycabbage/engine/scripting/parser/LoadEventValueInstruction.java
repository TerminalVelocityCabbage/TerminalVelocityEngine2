package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptEventValue;

public final class LoadEventValueInstruction implements ScriptInstruction {

    private final ScriptEventValue<?, ?> value;
    private final int targetSlot;

    public LoadEventValueInstruction(
            ScriptEventValue<?, ?> value,
            int targetSlot
    ) {
        this.value = value;
        this.targetSlot = targetSlot;
    }

    @Override
    public void execute(ExecutionContext context) {
        Object event = context.getEvent();
        Object extracted = value.extract(event);
        context.setLocal(targetSlot, extracted);
    }
}

