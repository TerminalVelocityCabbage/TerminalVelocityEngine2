package com.terminalvelocitycabbage.engine.util;

/**
 * A simple class to represent a toggleable yes/no status
 */
public class Toggle {

    boolean status;

    public Toggle() {
        this(true);
    }

    /**
     * @param status The initial status of this Toggle
     */
    public Toggle(boolean status) {
        this.status = status;
    }

    /**
     * @return The status of this toggle
     */
    public boolean getStatus() {
        return status;
    }

    /**
     * Set the status of this toggle to the
     * @param status boolean status
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
     * Sets the status of this toggle to enabled
     */
    public void enable() {
        setStatus(true);
    }

    /**
     * Sets the status of this toggle to disabled
     */
    public void disable() {
        setStatus(false);
    }
}
