package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;
import com.terminalvelocitycabbage.engine.client.input.types.ButtonAction;

public abstract non-sealed class BooleanController extends Controller {

    ButtonAction action;
    float amount;

    public BooleanController(ButtonAction action, boolean defaultState, Control[] controls) {
        this.action = action;
        this.amount = defaultState ? 1.0f : 0.0f;
        this.controlGroups = new ControlGroup[1];
        this.controlGroups[0] = new ControlGroup(controls);
    }

    @Override
    public void preProcess() {
        super.preProcess();
        amount = 0.0f;
    }

    @Override
    protected void processKeyControlInput(KeyboardKeyControl kkc) {
        if (switch (action) {
            case PRESSED -> kkc.isPressed();
            case RELEASED -> kkc.isReleased();
            case REPEAT -> kkc.isHolding();
            case INVALID -> false;
        }) amount = 1.0f;
    }

    @Override
    protected void processGamepadButtonControl(GamepadButtonControl gbc) {
        if (switch (action) {
            case PRESSED -> gbc.isPressed();
            case RELEASED -> gbc.isReleased();
            case REPEAT -> gbc.isHolding();
            case INVALID -> false;
        }) amount = 1.0f;
    }

    @Override
    protected void processMouseButtonControls(MouseButtonControl mbc) {
        if (switch (action) {
            case PRESSED -> mbc.isPressed();
            case RELEASED -> mbc.isReleased();
            case REPEAT -> mbc.isHolding();
            case INVALID -> false;
        }) amount = 1.0f;
    }

    @Override
    protected void processGamepadAxisControls(GamepadAxisControl gpac) {
        if (switch (action) {
            case PRESSED -> gpac.getAmount() > 0.95f;
            case RELEASED -> gpac.getAmount() < 0.05f;
            case REPEAT -> gpac.getHoldTime() > 0;
            case INVALID -> false;
        }) amount = 1.0f;
    }

    public boolean isEnabled() {
        return amount > 0.95f;
    }
}
