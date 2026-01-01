package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptProperty;

public final class LoadPropertyInstruction implements ScriptInstruction {

    private final int sourceSlot;
    private final int targetSlot;
    private final ScriptProperty<?> property;

    public LoadPropertyInstruction(
            int sourceSlot,
            int targetSlot,
            ScriptProperty<?> property
    ) {
        this.sourceSlot = sourceSlot;
        this.targetSlot = targetSlot;
        this.property = property;
    }

    @Override
    public void execute(ExecutionContext context) {
        Object source = context.getLocal(sourceSlot);
        Object value = property.get(source);
        context.setLocal(targetSlot, value);
    }
}

