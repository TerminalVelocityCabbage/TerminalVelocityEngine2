package com.terminalvelocitycabbage.engine.scripting.api;

import com.terminalvelocitycabbage.engine.registry.Identifiable;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.scripting.api.syntax.SyntaxPattern;

import java.util.List;
import java.util.function.Consumer;

public record ScriptAction(Identifier identifier, List<SyntaxPattern> patterns, ScriptType returnType,
                           Consumer<ActionContext> executor, String documentation) implements Identifiable {

    public void execute(ActionContext context) {
        executor.accept(context);
    }

    public static Builder builder(Identifier id) {
        return new Builder(id);
    }

    public static Builder builder(String namespace, String name) {
        return new Builder(new Identifier(namespace, name));
    }

    public void invoke(ActionContext arguments) {
        executor.accept(arguments);
    }

    @Override
    public Identifier getIdentifier() {
        return identifier;
    }

    public static final class Builder {
        private final Identifier identifier;
        private List<SyntaxPattern> patterns;
        private ScriptType returnType;
        private Consumer<ActionContext> executor;
        private String documentation = "";

        private Builder(Identifier id) {
            this.identifier = id;
        }

        public Builder patterns(List<SyntaxPattern> patterns) {
            this.patterns = patterns;
            return this;
        }

        public Builder returns(ScriptType returnType) {
            this.returnType = returnType;
            return this;
        }

        public Builder exec(Consumer<ActionContext> executor) {
            this.executor = executor;
            return this;
        }

        public Builder doc(String documentation) {
            this.documentation = documentation;
            return this;
        }

        public ScriptAction build() {
            if (patterns == null || executor == null) {
                throw new IllegalStateException("Action must have patterns and executor");
            }
            return new ScriptAction(identifier, patterns, returnType, executor, documentation);
        }
    }
}
