package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.GamepadInput;
import com.terminalvelocitycabbage.engine.client.input.util.GamepadInputUtil;

/**
 * A Control which is listening to buttons on a gamepad (controller mapped to xbox layout)
 */
public non-sealed class GamepadButtonControl extends ButtonControl {

    //The button on the gamepad which this Control listens to
    GamepadInput.Button button;

    public GamepadButtonControl(GamepadInput.Button button) {
        this.button = button;
    }

    @Override
    public boolean isPressed(InputHandler inputHandler) {
        return GamepadInputUtil.isButtonPressed(inputHandler.getGamepadState(), button);
    }

    /**
     * @return The button on the gamepad which this Control is listening to
     */
    public GamepadInput.Button getButton() {
        return button;
    }
}
