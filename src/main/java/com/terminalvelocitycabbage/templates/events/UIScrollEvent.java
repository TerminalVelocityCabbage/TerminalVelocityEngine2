package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.joml.Vector2f;

public class UIScrollEvent extends UIInputEvent {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "ui_scroll");

    private final Vector2f delta;

    public UIScrollEvent(Vector2f delta) {
        super(EVENT);
        this.delta = delta;
    }

    public Vector2f getDelta() {
        return delta;
    }
}
