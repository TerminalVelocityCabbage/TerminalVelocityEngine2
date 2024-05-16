package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.KeyboardInput;
import com.terminalvelocitycabbage.engine.client.input.util.KeyboardInputUtil;

public non-sealed class KeyboardKeyControl extends ButtonControl {

    KeyboardInput key;

    public KeyboardKeyControl(KeyboardInput key) {
        this.key = key;
    }

    @Override
    public boolean isPressed(InputHandler inputHandler) {
        return KeyboardInputUtil.isKeyPressed(inputHandler.getFocusedWindow(), key.getGlfwKey());
    }

    public KeyboardInput getKey() {
        return key;
    }
}
