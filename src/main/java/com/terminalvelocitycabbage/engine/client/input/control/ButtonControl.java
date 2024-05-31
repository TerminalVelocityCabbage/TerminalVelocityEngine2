package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;

/**
 * This is an abstract class which defines logic common to all Control types that can be described as buttons, it is
 * a control which can only be either on or off, but is still mapped to a 0-1 float value, but can only be 0 or 1
 */
public abstract sealed class ButtonControl extends Control permits KeyboardKeyControl, MouseButtonControl, GamepadButtonControl {

    public ButtonControl() {
        super(1);
    }

    @Override
    public void update(InputHandler inputHandler, long deltaTime) {
        pressedThisFrame = isPressed(inputHandler);
        if (!pressedLastFrame) holdTime = 0;
        if (pressedThisFrame && pressedLastFrame) {
            holdTime += deltaTime;
        }
        pressedLastFrame = pressedThisFrame;
    }

    @Override
    public float getAmount() {
        //Since this is a button, it can only be either on or off, but all Controls are mapped from 0 to 1, so convert.
        return isPressed() ? 1 : 0;
    }

    /**
     * @param inputHandler The input handler which this Control belongs to
     * @return a boolean whether this control is active this frame.
     */
    public abstract boolean isPressed(InputHandler inputHandler);
}
