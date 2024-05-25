package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.GamepadInput;
import com.terminalvelocitycabbage.engine.client.input.util.GamepadInputUtil;

public non-sealed class GamepadAxisControl extends Control {

    GamepadInput.Axis axis;

    public GamepadAxisControl(GamepadInput.Axis axis, float sensitivity) {
        super(sensitivity);
        this.axis = axis;
    }

    @Override
    public void update(InputHandler inputHandler, long deltaTime) {
        amount = GamepadInputUtil.getAxisAmount(axis, inputHandler.getGamepadState());
    }

    public GamepadInput.Axis getAxis() {
        return axis;
    }
}
