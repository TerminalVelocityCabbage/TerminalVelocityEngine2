package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.GamepadInput;
import com.terminalvelocitycabbage.engine.client.input.util.GamepadInputUtil;

public non-sealed class GamepadButtonControl extends Control {

    boolean pressedLastFrame = false;
    boolean pressedThisFrame = false;
    long holdTime = 0;
    GamepadInput.Button button;

    public GamepadButtonControl(GamepadInput.Button button) {
        this.button = button;
    }

    @Override
    public void update(InputHandler inputHandler, long deltaTime) {
        pressedLastFrame = pressedThisFrame;
        pressedThisFrame = GamepadInputUtil.isButtonPressed(inputHandler.getGamepadState(), button);
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

    public GamepadInput.Button getButton() {
        return button;
    }
}
