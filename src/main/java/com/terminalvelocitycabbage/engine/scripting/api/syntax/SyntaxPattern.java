package com.terminalvelocitycabbage.engine.scripting.api.syntax;

import java.util.List;

public final class SyntaxPattern {

    private final List<SyntaxPart> parts;

    public SyntaxPattern(List<SyntaxPart> parts) {
        this.parts = parts;
    }

    public List<SyntaxPart> parts() {
        return parts;
    }

    public static SyntaxPattern of(SyntaxPart... parts) {
        return new SyntaxPattern(List.of(parts));
    }
}



