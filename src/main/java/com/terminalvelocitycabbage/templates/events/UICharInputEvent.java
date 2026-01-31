package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class UICharInputEvent extends UIInputEvent {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "ui_char_input");

    private final int character;

    public UICharInputEvent(int character) {
        super(EVENT);
        this.character = character;
    }

    public int getCharacter() {
        return character;
    }

    public String getCharacterString() {
        return String.copyValueOf(Character.toChars(character));
    }
}
