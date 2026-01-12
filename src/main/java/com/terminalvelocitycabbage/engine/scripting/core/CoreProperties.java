package com.terminalvelocitycabbage.engine.scripting.core;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.api.PropertyAccess;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptProperty;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptVisibility;
import com.terminalvelocitycabbage.engine.scripting.api.registry.ScriptPropertyRegistry;

public final class CoreProperties {

    public static void register(ScriptPropertyRegistry properties) {

        properties.register(
                new ScriptProperty<>(
                        new Identifier("core", "identifier.namespace"),
                        CoreTypes.IDENTIFIER,
                        CoreTypes.TEXT,
                        new PropertyAccess.ReadOnly<>(Identifier::getNamespace),
                        ScriptVisibility.PUBLIC,
                        "The namespace portion of the identifier."
                )
        );

        properties.register(
                new ScriptProperty<>(
                        new Identifier("core", "identifier.name"),
                        CoreTypes.IDENTIFIER,
                        CoreTypes.TEXT,
                        new PropertyAccess.ReadOnly<>(Identifier::getName),
                        ScriptVisibility.PUBLIC,
                        "The name/path portion of the identifier."
                )
        );
    }
}

