package com.terminalvelocitycabbage.engine.scripting.parser;

public final class ExecutionContext {

    private final Object[] locals;

    public ExecutionContext(Object[] locals) {
        this.locals = locals;
    }

    public Object getLocal(int index) {
        return locals[index];
    }

    public void setLocal(int index, Object value) {
        locals[index] = value;
    }
}

