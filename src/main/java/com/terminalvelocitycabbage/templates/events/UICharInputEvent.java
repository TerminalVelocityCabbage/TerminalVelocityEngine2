package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class UICharInputEvent extends UIInputEvent {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "ui_char_input");

    private final int character;
    private final SpecialInputKey specialInputKey;

    public UICharInputEvent(int character, SpecialInputKey specialInputKey) {
        super(EVENT);
        this.character = character;
        this.specialInputKey = specialInputKey;
    }

    public boolean isSpecialInput() {
        return specialInputKey != null;
    }

    public boolean isBackspace() {
        return isSpecialInput() && specialInputKey.isBackspace();
    }

    public boolean isDelete() {
        return isSpecialInput() && specialInputKey.isDelete();
    }

    public boolean isEnter() {
        return isSpecialInput() && specialInputKey.isEnter();
    }

    public boolean isTab() {
        return isSpecialInput() && specialInputKey.isTab();
    }

    public boolean isShift() {
        return isSpecialInput() && specialInputKey.isShift();
    }

    public boolean isCtrl() {
        return isSpecialInput() && specialInputKey.isCtrl();
    }

    public boolean isAlt() {
        return isSpecialInput() && specialInputKey.isAlt();
    }

    public int getCharacter() {
        return character;
    }

    public String getCharacterString() {
        return String.copyValueOf(Character.toChars(character));
    }

    public record SpecialInputKey(boolean isBackspace, boolean isDelete, boolean isEnter, boolean isTab, boolean isShift, boolean isCtrl, boolean isAlt) { }
}
