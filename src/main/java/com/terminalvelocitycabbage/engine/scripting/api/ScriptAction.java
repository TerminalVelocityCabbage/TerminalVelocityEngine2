package com.terminalvelocitycabbage.engine.scripting.api;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.api.syntax.SyntaxPattern;

import java.util.List;

public final class ScriptAction {

    private final Identifier id;
    private final List<SyntaxPattern> patterns;
    private final ScriptType returnType;
    private final ActionExecutor executor;
    private final String documentation;

    public ScriptAction(
            Identifier id,
            List<SyntaxPattern> patterns,
            ScriptType returnType,
            ActionExecutor executor,
            String documentation
    ) {
        this.id = id;
        this.patterns = patterns;
        this.returnType = returnType;
        this.executor = executor;
        this.documentation = documentation;
    }

    public Identifier id() {
        return id;
    }

    public List<SyntaxPattern> patterns() {
        return patterns;
    }

    public ScriptType returnType() {
        return returnType;
    }

    public ActionExecutor executor() {
        return executor;
    }
}

