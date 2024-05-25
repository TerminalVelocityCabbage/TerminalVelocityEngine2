package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;

public sealed abstract class Control permits ButtonControl, GamepadAxisControl, MouseMovementControl, MouseScrollControl {

    float amount;
    float sensitivity;

    boolean pressedLastFrame = false;
    boolean pressedThisFrame = false;
    long holdTime = 0;

    public Control(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    public abstract void update(InputHandler inputHandler, long deltaTime);

    public boolean isPressed() {
        return pressedThisFrame;
    }

    public float getAmount() {
        return amount * sensitivity;
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

    public float getSensitivity() {
        return sensitivity;
    }

    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }
}
