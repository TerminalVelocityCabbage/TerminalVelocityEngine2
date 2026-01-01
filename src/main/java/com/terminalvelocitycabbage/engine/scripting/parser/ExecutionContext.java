package com.terminalvelocitycabbage.engine.scripting.parser;

public final class ExecutionContext {

    private final Object event;
    private final Object[] locals;

    public ExecutionContext(Object event, int localCount) {
        this.event = event;
        this.locals = new Object[localCount];
    }

    public Object getEvent() {
        return event;
    }

    public Object getLocal(int slot) {
        return locals[slot];
    }

    public void setLocal(int slot, Object value) {
        locals[slot] = value;
    }
}


