package com.terminalvelocitycabbage.engine.scripting.parser;

import com.terminalvelocitycabbage.engine.scripting.api.ActionContext;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptAction;

import java.util.Map;

public final class CallActionInstruction implements ScriptInstruction {

    private final ScriptAction action;
    private final int[] argumentSlots;
    private final Map<String, Integer> argumentNameToIndex;
    private final int resultSlot; // -1 for void

    public CallActionInstruction(
            ScriptAction action,
            int[] argumentSlots,
            Map<String, Integer> argumentNameToIndex,
            int resultSlot
    ) {
        this.action = action;
        this.argumentSlots = argumentSlots;
        this.argumentNameToIndex = argumentNameToIndex;
        this.resultSlot = resultSlot;
    }

    @Override
    public void execute(ExecutionContext executionContext) {

        ActionContext actionContext = new ActionContext(
                executionContext,
                argumentSlots,
                argumentNameToIndex
        );

        action.execute(actionContext);
    }
}

