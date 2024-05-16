package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;

public sealed abstract class Control permits ButtonControl, GamepadAxisControl {

    float amount;

    boolean pressedLastFrame = false;
    boolean pressedThisFrame = false;
    long holdTime = 0;

    public abstract void update(InputHandler inputHandler, long deltaTime);

    public boolean isPressed() {
        return pressedThisFrame;
    }

    public float getAmount() {
        return amount;
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
