package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.GamepadInput;
import com.terminalvelocitycabbage.engine.client.input.types.MouseButtonInput;
import com.terminalvelocitycabbage.engine.client.input.util.GamepadInputUtil;
import com.terminalvelocitycabbage.engine.client.input.util.MouseInputUtil;

public non-sealed class MouseButtonControl extends Control {

    boolean pressedLastFrame = false;
    boolean pressedThisFrame = false;
    long holdTime = 0;
    MouseButtonInput button;

    public MouseButtonControl(MouseButtonInput button) {
        this.button = button;
    }

    @Override
    public void update(InputHandler inputHandler, long deltaTime) {
        pressedLastFrame = pressedThisFrame;
        pressedThisFrame = MouseInputUtil.isMouseButtonPressed(inputHandler.getMousedOverWindow(), button);
        if (!pressedLastFrame) holdTime = 0;
        if (pressedThisFrame && pressedLastFrame) {
            holdTime += deltaTime;
        }
    }

    public boolean isPressed() {
        return pressedThisFrame;
    }

    public boolean isReleased() {
        return pressedLastFrame && !pressedThisFrame;
    }

    public boolean isHolding() {
        return pressedLastFrame && pressedThisFrame;
    }

    public long getHoldTime() {
        return holdTime;
    }

    public MouseButtonInput getButton() {
        return button;
    }
}
