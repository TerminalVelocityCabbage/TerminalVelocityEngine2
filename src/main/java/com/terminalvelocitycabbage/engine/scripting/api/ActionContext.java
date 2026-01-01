package com.terminalvelocitycabbage.engine.scripting.api;

import com.terminalvelocitycabbage.engine.scripting.parser.ExecutionContext;

import java.util.Map;

public final class ActionContext {

    private final ExecutionContext execution;
    private final int[] argumentSlots;
    private final Map<String, Integer> nameToIndex;

    public ActionContext(
            ExecutionContext execution,
            int[] argumentSlots,
            Map<String, Integer> nameToIndex
    ) {
        this.execution = execution;
        this.argumentSlots = argumentSlots;
        this.nameToIndex = nameToIndex;
    }

    public Object get(int index) {
        return execution.getLocal(argumentSlots[index]);
    }

    public Object get(String name) {
        Integer index = nameToIndex.get(name);
        if (index == null) {
            throw new RuntimeException("Unknown argument: " + name);
        }
        return get(index);
    }

    public boolean isPresent(String name) {
        return nameToIndex.containsKey(name);
    }

    public Object getEvent() {
        return execution.getEvent();
    }

    public void setResult(Object value, int resultSlot) {
        if (resultSlot >= 0) {
            execution.setLocal(resultSlot, value);
        }
    }
}

