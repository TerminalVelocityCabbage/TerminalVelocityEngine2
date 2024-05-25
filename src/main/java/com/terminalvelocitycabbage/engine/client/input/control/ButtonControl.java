package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;

public abstract sealed class ButtonControl extends Control permits KeyboardKeyControl, MouseButtonControl, GamepadButtonControl {

    public ButtonControl() {
        super(1);
    }

    @Override
    public void update(InputHandler inputHandler, long deltaTime) {
        pressedLastFrame = pressedThisFrame;
        pressedThisFrame = isPressed(inputHandler);
        if (!pressedLastFrame) holdTime = 0;
        if (pressedThisFrame && pressedLastFrame) {
            holdTime += deltaTime;
        }
    }

    @Override
    public float getAmount() {
        return isPressed() ? 1 : 0;
    }

    public abstract boolean isPressed(InputHandler inputHandler);
}
