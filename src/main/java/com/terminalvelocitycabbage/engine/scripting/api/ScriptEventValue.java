package com.terminalvelocitycabbage.engine.scripting.api;

import java.util.function.Function;

public final class ScriptEventValue<E, V> {

    private final String name;
    private final ScriptType type;
    private final Function<E, V> extractor;

    public ScriptEventValue(
            String name,
            ScriptType type,
            Function<E, V> extractor
    ) {
        this.name = name;
        this.type = type;
        this.extractor = extractor;
    }

    @SuppressWarnings("unchecked")
    public Object extract(Object eventInstance) {
        return extractor.apply((E) eventInstance);
    }

    public ScriptType getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}


