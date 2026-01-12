package com.terminalvelocitycabbage.engine.scripting.core;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptConstant;
import com.terminalvelocitycabbage.engine.scripting.api.registry.ScriptConstantRegistry;

public final class CoreConstants {

    public static void register(ScriptConstantRegistry constants) {

        constants.register(new ScriptConstant(
                new Identifier(CoreLibrary.CORE_NAMESPACE, "true"),
                CoreTypes.BOOLEAN,
                Boolean.TRUE,
                "Boolean true"
        ));

        constants.register(new ScriptConstant(
                new Identifier(CoreLibrary.CORE_NAMESPACE, "false"),
                CoreTypes.BOOLEAN,
                Boolean.FALSE,
                "Boolean false"
        ));
    }
}

