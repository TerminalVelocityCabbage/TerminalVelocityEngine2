package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.input.types.MouseInput;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.joml.Vector2f;

public class UIClickEvent extends UIInputEvent {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("ui_click");

    private final Vector2f position;
    private final MouseInput.Button button;

    public UIClickEvent(Vector2f position, MouseInput.Button button) {
        super(EVENT);
        this.position = position;
        this.button = button;
    }

    public Vector2f getPosition() {
        return position;
    }

    public MouseInput.Button getButton() {
        return button;
    }
}
