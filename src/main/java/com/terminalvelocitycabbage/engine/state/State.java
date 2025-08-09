package com.terminalvelocitycabbage.engine.state;

public class State {

    boolean enabled;
    boolean wasEnabledLastTick;

    public State(boolean enabled) {
        this.enabled = enabled;
        this.wasEnabledLastTick = false;
    }

    public boolean toggle() {
        enabled = !enabled;
        return enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isDisabled() {
        return !enabled;
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
    }

    protected void tick() {
        wasEnabledLastTick = enabled;
    }

    public boolean wasEnabledLastTick() {
        return wasEnabledLastTick;
    }
}
