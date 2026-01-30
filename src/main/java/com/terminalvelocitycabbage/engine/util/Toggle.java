package com.terminalvelocitycabbage.engine.util;

/**
 * A simple class to represent a toggleable yes/no type
 */
public class Toggle {

    boolean status;

    public Toggle() {
        this(true);
    }

    /**
     * @param status The initial type of this Toggle
     */
    public Toggle(boolean status) {
        this.status = status;
    }

    /**
     * @return The type of this toggle
     */
    public boolean getStatus() {
        return status;
    }

    /**
     * Set the type of this toggle to the
     * @param status boolean type
     */
    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * Toggle this toggle to it's opposite state
     */
    public void toggle() {
        setStatus(!getStatus());
    }

    /**
     * Sets the type of this toggle to enabled
     */
    public void enable() {
        setStatus(true);
    }

    /**
     * Sets the type of this toggle to disabled
     */
    public void disable() {
        setStatus(false);
    }
}
