package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.MouseInput;
import com.terminalvelocitycabbage.engine.client.input.util.MouseInputUtil;

/**
 * A Control type which listens to the change in position of the mouse
 */
public non-sealed class MouseMovementControl extends Control {

    //The Movement Axis that this Control Listens for
    MouseInput.MovementAxis axis;

    public MouseMovementControl(final MouseInput.MovementAxis axis, float sensitivity) {
        super(sensitivity);
        this.axis = axis;
    }

    @Override
    public void update(InputHandler inputHandler, long deltaTime) {
        amount = MouseInputUtil.getAxisAmount(axis);
    }

    /**
     * @return The Movement Axis that this Control Listens for
     */
    public MouseInput.MovementAxis getAxis() {
        return axis;
    }

    /**
     * @return Whether the mouse moved between last frame and this one
     */
    public boolean hasMoved() {
        return amount > 0f;
    }
}
