package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.GamepadInput;
import com.terminalvelocitycabbage.engine.client.input.util.GamepadInputUtil;

/**
 * A control received from a Gamepad Axis, this is usually a Joystick or trigger, mapped to a value from 0 to 1
 */
public non-sealed class GamepadAxisControl extends Control {

    //The axis that this Control listens to
    GamepadInput.Axis axis;

    public GamepadAxisControl(GamepadInput.Axis axis, float sensitivity) {
        super(sensitivity);
        this.axis = axis;
    }

    /**
     * @param inputHandler The input handler which this Control is registered to
     * @param deltaTime    The time between now and the last time the input handler ticked
     */
    @Override
    public void update(InputHandler inputHandler, long deltaTime) {
        amount = GamepadInputUtil.getAxisAmount(axis, inputHandler.getGamepadState());
    }

    /**
     * @return The Axis that this Control is listening to
     */
    public GamepadInput.Axis getAxis() {
        return axis;
    }
}
