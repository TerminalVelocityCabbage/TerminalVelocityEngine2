package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.types.KeyboardInput;

public non-sealed class KeyboardKeyControl extends Control {

    KeyboardInput key;

    public KeyboardKeyControl(KeyboardInput key) {
        this.key = key;
    }

    public boolean isPressed() {
        return false;
    }

}
