package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.KeyboardInput;
import com.terminalvelocitycabbage.engine.client.input.util.KeyboardInputUtil;

/**
 * A Control type which listens to keys on the keyboard see {@link KeyboardInput}
 */
public non-sealed class KeyboardKeyControl extends ButtonControl {

    //The Key on the keyboard which this Control is listening to
    KeyboardInput.Key key;

    public KeyboardKeyControl(KeyboardInput.Key key) {
        this.key = key;
    }

    @Override
    public boolean isPressed(InputHandler inputHandler) {
        return KeyboardInputUtil.isKeyPressed(inputHandler.getFocusedWindow(), key.getGlfwKey());
    }

    /**
     * @return The Key on the keyboard which this Control is listening to
     */
    public KeyboardInput.Key getKey() {
        return key;
    }
}
