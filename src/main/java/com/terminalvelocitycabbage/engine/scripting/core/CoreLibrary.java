package com.terminalvelocitycabbage.engine.scripting.core;

import com.terminalvelocitycabbage.engine.scripting.api.registry.*;

public final class CoreLibrary {

    public static final String CORE_NAMESPACE = "core";

    private CoreLibrary() {}

    public static void registerAll(
            ScriptTypeRegistry types,
            ScriptActionRegistry actions,
            ScriptPropertyRegistry properties,
            ScriptEventRegistry events,
            ScriptConstantRegistry constants
    ) {
        CoreTypes.register(types);
        CoreActions.register(actions);
        CoreProperties.register(properties);
        CoreConstants.register(constants);
        CoreEvents.register(events);
    }
}

