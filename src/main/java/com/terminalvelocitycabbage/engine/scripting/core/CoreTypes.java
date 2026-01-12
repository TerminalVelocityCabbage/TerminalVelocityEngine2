package com.terminalvelocitycabbage.engine.scripting.core;

import com.terminalvelocitycabbage.engine.scripting.api.ScriptType;
import com.terminalvelocitycabbage.engine.scripting.api.registry.ScriptTypeRegistry;

public final class CoreTypes {

    public static ScriptType ANY;
    public static ScriptType VOID;

    public static ScriptType NUMBER;
    public static ScriptType INTEGER;
    public static ScriptType BOOLEAN;
    public static ScriptType TEXT;

    public static ScriptType IDENTIFIER;

    public static ScriptType LIST;
    public static ScriptType MAP;

    public static void register(ScriptTypeRegistry registry) {
        ANY = registry.register(ScriptType.of(CoreLibrary.CORE_NAMESPACE, "any")).getElement();
        VOID = registry.register(ScriptType.of(CoreLibrary.CORE_NAMESPACE, "void")).getElement();

        NUMBER = registry.register(ScriptType.of(CoreLibrary.CORE_NAMESPACE, "number", ANY)).getElement();
        INTEGER = registry.register(ScriptType.of(CoreLibrary.CORE_NAMESPACE, "integer", NUMBER)).getElement();
        BOOLEAN = registry.register(ScriptType.of(CoreLibrary.CORE_NAMESPACE, "boolean", ANY)).getElement();
        TEXT = registry.register(ScriptType.of(CoreLibrary.CORE_NAMESPACE, "text", ANY)).getElement();

        IDENTIFIER = registry.register(ScriptType.of(CoreLibrary.CORE_NAMESPACE, "identifier", ANY)).getElement();

        LIST = registry.register(ScriptType.of(CoreLibrary.CORE_NAMESPACE, "list", ANY)).getElement();
        MAP = registry.register(ScriptType.of(CoreLibrary.CORE_NAMESPACE, "map", ANY)).getElement();
    }
}

