package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.Control;
import com.terminalvelocitycabbage.engine.client.input.control.GamepadButtonControl;
import com.terminalvelocitycabbage.engine.client.input.control.KeyboardKeyControl;
import com.terminalvelocitycabbage.engine.client.input.control.MouseButtonControl;

public abstract non-sealed class ToggleController extends Controller {

    boolean enabled;

    public ToggleController(Control[] controls) {
        super(controls);
    }

    @Override
    public void preProcess() {
        super.preProcess();
        enabled = false;
    }

    @Override
    protected void processKeyControlInput(KeyboardKeyControl kkc) {
        if (kkc.isPressed()) {
            enabled = true;
        }
    }

    @Override
    protected void processGamepadButtonControl(GamepadButtonControl gbc) {
        if (gbc.isPressed()) {
            enabled = true;
        }
    }

    @Override
    protected void processMouseButtonControls(MouseButtonControl mbc) {
        if (mbc.isPressed()) {
            enabled = true;
        }
    }

    public boolean isEnabled() {
        return enabled;
    }
}
