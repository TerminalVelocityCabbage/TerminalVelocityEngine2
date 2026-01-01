package com.terminalvelocitycabbage.engine.scripting.api.syntax;

import java.util.List;

public record SyntaxPattern(List<SyntaxElement> elements) {

    public SyntaxPattern(List<SyntaxElement> elements) {
        this.elements = List.copyOf(elements);
    }

}
