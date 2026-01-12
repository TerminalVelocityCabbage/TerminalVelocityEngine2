package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptProperty;

public final class LoadPropertyInstruction implements ScriptInstruction {

    private final ScriptProperty<?, ?> property;
    private final int ownerSlot;
    private final int targetSlot;

    public LoadPropertyInstruction(ScriptProperty<?, ?> property, int ownerSlot, int targetSlot) {
        this.property = property;
        this.ownerSlot = ownerSlot;
        this.targetSlot = targetSlot;
    }

    @Override
    public void execute(ExecutionContext context) {
        Object owner = context.getLocal(ownerSlot);
        Object value = property.get(owner);
        context.setLocal(targetSlot, value);
    }
}

