package com.terminalvelocitycabbage.engine.scripting.core;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptAction;
import com.terminalvelocitycabbage.engine.scripting.api.registry.ScriptActionRegistry;
import com.terminalvelocitycabbage.engine.scripting.api.syntax.SyntaxArgument;
import com.terminalvelocitycabbage.engine.scripting.api.syntax.SyntaxLiteral;
import com.terminalvelocitycabbage.engine.scripting.api.syntax.SyntaxPattern;

import java.util.List;

public final class CoreActions {

    public static void register(ScriptActionRegistry registry) {

        ScriptAction print =
                new ScriptAction(
                        new Identifier(CoreLibrary.CORE_NAMESPACE, "print"),
                        List.of(
                                SyntaxPattern.of(
                                        new SyntaxLiteral("print"),
                                        new SyntaxArgument("value", CoreTypes.TEXT)
                                )
                        ),
                        CoreTypes.VOID,
                        context -> {
                            String value = context.get("value");
                            System.out.println(value);
                        },
                        "Prints text to the console."
                );

        registry.register(print);
    }
}


