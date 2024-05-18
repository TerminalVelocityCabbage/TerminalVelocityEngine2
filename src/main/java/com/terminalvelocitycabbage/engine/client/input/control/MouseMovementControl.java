package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.MouseInput;
import com.terminalvelocitycabbage.engine.client.input.util.MouseInputUtil;

public non-sealed class MouseMovementControl extends Control {

    MouseInput.MovementAxis axis;

    public MouseMovementControl(final MouseInput.MovementAxis axis) {
        this.axis = axis;
    }

    @Override
    public void update(InputHandler inputHandler, long deltaTime) {
        amount = MouseInputUtil.getAxisAmount(axis);
    }

    public MouseInput.MovementAxis getAxis() {
        return axis;
    }

    public boolean hasMoved() {
        return amount > 0f;
    }
}
