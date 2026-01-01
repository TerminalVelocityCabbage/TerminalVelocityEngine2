package com.terminalvelocitycabbage.engine.scripting.api;

import com.terminalvelocitycabbage.engine.registry.Identifiable;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public final class ScriptProperty<O, V> implements Identifiable {

    private final Identifier identifier;
    private final ScriptType ownerType;
    private final ScriptType valueType;
    private final PropertyAccess<O, V> access;
    private final ScriptVisibility visibility;
    private final String documentation;

    public ScriptProperty(
            Identifier id,
            ScriptType ownerType,
            ScriptType valueType,
            PropertyAccess<O, V> access,
            ScriptVisibility visibility,
            String documentation
    ) {
        this.identifier = id;
        this.ownerType = ownerType;
        this.valueType = valueType;
        this.access = access;
        this.visibility = visibility;
        this.documentation = documentation;
    }

    @SuppressWarnings("unchecked")
    public Object get(Object instance) {
        return access.get((O) instance);
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }
}


