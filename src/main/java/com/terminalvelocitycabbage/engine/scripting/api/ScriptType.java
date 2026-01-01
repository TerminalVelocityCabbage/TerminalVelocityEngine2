package com.terminalvelocitycabbage.engine.scripting.api;

import com.terminalvelocitycabbage.engine.registry.Identifiable;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.Objects;

public final class ScriptType implements Identifiable {

    private final Identifier identifier;
    private final ScriptType parent;

    private ScriptType(String namespace, String name, ScriptType parent) {
        this.identifier = new Identifier(namespace, name);
        this.parent = parent;
    }

    public static ScriptType of(String namespace, String name) {
        return new ScriptType(namespace, name, null);
    }

    public static ScriptType of(String namespace, String name, ScriptType parent) {
        return new ScriptType(namespace, name, parent);
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    public ScriptType getParent() {
        return parent;
    }

    public boolean isAssignableFrom(ScriptType other) {
        if (this.equals(other)) return true;
        ScriptType current = other.parent;
        while (current != null) {
            if (this.equals(current)) return true;
            current = current.parent;
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ScriptType other)) return false;
        return identifier.equals(other.getIdentifier());
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return identifier.toString();
    }
}
