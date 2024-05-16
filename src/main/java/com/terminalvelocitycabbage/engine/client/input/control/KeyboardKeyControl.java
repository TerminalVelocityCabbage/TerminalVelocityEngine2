package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.KeyboardInput;
import com.terminalvelocitycabbage.engine.client.input.util.KeyboardInputUtil;

public non-sealed class KeyboardKeyControl extends Control {

    boolean pressedLastFrame = false;
    boolean pressedThisFrame = false;
    long holdTime = 0;
    KeyboardInput key;

    public KeyboardKeyControl(KeyboardInput key) {
        this.key = key;
    }

    @Override
    public void update(InputHandler inputHandler, long deltaTime) {
        pressedLastFrame = pressedThisFrame;
        pressedThisFrame = KeyboardInputUtil.isKeyPressed(inputHandler.getFocusedWindow(), key.getGlfwKey());
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

}
