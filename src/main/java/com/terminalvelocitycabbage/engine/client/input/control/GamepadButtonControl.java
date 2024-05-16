package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.GamepadInput;
import com.terminalvelocitycabbage.engine.client.input.util.GamepadInputUtil;

public non-sealed class GamepadButtonControl extends ButtonControl {

    GamepadInput.Button button;

    public GamepadButtonControl(GamepadInput.Button button) {
        this.button = button;
    }

    @Override
    public boolean isPressed(InputHandler inputHandler) {
        return GamepadInputUtil.isButtonPressed(inputHandler.getGamepadState(), button);
    }

    public GamepadInput.Button getButton() {
        return button;
    }
}
