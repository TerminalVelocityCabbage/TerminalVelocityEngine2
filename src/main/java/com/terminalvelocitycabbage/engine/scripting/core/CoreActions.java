package com.terminalvelocitycabbage.engine.scripting.core;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.api.ScriptAction;
import com.terminalvelocitycabbage.engine.scripting.api.registry.ScriptActionRegistry;
import com.terminalvelocitycabbage.engine.scripting.api.syntax.Syntax;
import com.terminalvelocitycabbage.engine.scripting.api.syntax.SyntaxPattern;

import java.util.List;

public final class CoreActions {

    public static void register(ScriptActionRegistry actions) {
        actions.register(
                ScriptAction.builder(new Identifier(CoreLibrary.CORE_NAMESPACE, "print"))
                        .patterns(List.of(
                                new SyntaxPattern(List.of(
                                        Syntax.literal("print"),
                                        Syntax.argument("value", CoreTypes.ANY)
                                ))
                        ))
                        .returns(CoreTypes.VOID)
                        .exec(ctx -> {
                            Object value = ctx.get("value");
                            System.out.println(value);
                        })
                        .doc("Prints a value to the console.")
                        .build()
        );
    }
}

