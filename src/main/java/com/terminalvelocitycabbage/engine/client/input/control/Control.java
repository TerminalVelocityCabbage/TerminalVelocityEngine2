package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;

/**
 * The base class which all control types inherit from. This is what converts raw input into useful data that the user
 * can use to give input into the game
 */
public sealed abstract class Control permits ButtonControl, GamepadAxisControl, MouseMovementControl, MouseScrollControl {

    float amount;
    float sensitivity;

    boolean pressedLastFrame = false;
    boolean pressedThisFrame = false;
    long holdTime = 0;

    public Control(float sensitivity) {
        this.sensitivity = sensitivity;
    }

    /**
     * The method which updates the amount value of this Control
     * @param inputHandler The input handler which this Control is registered to
     * @param deltaTime The time between now and the last time the input handler ticked
     */
    public abstract void update(InputHandler inputHandler, long deltaTime);

    /**
     * @return The amount that this Control is active
     */
    public float getAmount() {
        return amount * sensitivity;
    }

    /**
     * @return Whether this key is being pressed
     */
    public boolean isPressed() {
        return pressedThisFrame;
    }

    /**
     * @return If this key was just released between the last frame and this one
     */
    public boolean isReleased() {
        return pressedLastFrame && !pressedThisFrame;
    }

    /**
     * @return If this key has been down longer than just this frame
     */
    public boolean isHolding() {
        return pressedLastFrame && pressedThisFrame;
    }

    /**
     * @return How long this frame has been down for
     */
    public long getHoldTime() {
        return holdTime;
    }

    /**
     * @return The sensitivity modifier for this Control
     */
    public float getSensitivity() {
        return sensitivity;
    }

    /**
     * Updates the modifier by which this Control's amount value will be multiplied when it is queried
     * @param sensitivity the sensitivity modifier you wish to be applied to this Control
     */
    public void setSensitivity(float sensitivity) {
        this.sensitivity = sensitivity;
    }
}
