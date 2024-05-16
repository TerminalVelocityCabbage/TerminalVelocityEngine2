package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.Control;
import com.terminalvelocitycabbage.engine.client.input.control.GamepadButtonControl;
import com.terminalvelocitycabbage.engine.client.input.control.KeyboardKeyControl;
import com.terminalvelocitycabbage.engine.client.input.control.MouseButtonControl;
import com.terminalvelocitycabbage.engine.client.input.types.ButtonAction;

public abstract non-sealed class ToggleController extends Controller {

    boolean enabled;
    ButtonAction action;

    public ToggleController(ButtonAction action, boolean defaultState, Control[] controls) {
        super(controls);
        this.action = action;
        this.enabled = defaultState;
    }

    @Override
    public void preProcess() {
        super.preProcess();
        enabled = false;
    }

    @Override
    protected void processKeyControlInput(KeyboardKeyControl kkc) {
        if (switch (action) {
            case PRESSED -> kkc.isPressed();
            case RELEASED -> kkc.isReleased();
            case REPEAT -> kkc.isHolding();
            case INVALID -> false;
        }) enabled = true;
    }

    @Override
    protected void processGamepadButtonControl(GamepadButtonControl gbc) {
        if (switch (action) {
            case PRESSED -> gbc.isPressed();
            case RELEASED -> gbc.isReleased();
            case REPEAT -> gbc.isHolding();
            case INVALID -> false;
        }) enabled = true;
    }

    @Override
    protected void processMouseButtonControls(MouseButtonControl mbc) {
        if (switch (action) {
            case PRESSED -> mbc.isPressed();
            case RELEASED -> mbc.isReleased();
            case REPEAT -> mbc.isHolding();
            case INVALID -> false;
        }) enabled = true;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
