package com.terminalvelocitycabbage.engine;

import com.terminalvelocitycabbage.engine.registry.Identifier;

public class TerminalVelocityEngine {

    public static final String ID = "terminalvelocityengine";

    public static Identifier identifierOf(String type, String name) {
        return new Identifier(ID, type, name);
    }
}
