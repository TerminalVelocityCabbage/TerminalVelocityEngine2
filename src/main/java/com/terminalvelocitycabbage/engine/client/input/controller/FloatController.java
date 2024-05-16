package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;

public non-sealed abstract class FloatController extends Controller {

    float amount;

    public FloatController(Control... controls) {
        super(controls);
        this.amount = 0f;
    }

    @Override
    protected void processKeyControlInput(KeyboardKeyControl kkc) {
        //TODO discuss should this result in 1 vs 0 from a key?
    }

    @Override
    protected void processGamepadButtonControl(GamepadButtonControl gbc) {
        //TODO discuss should this result in 1 vs 0 from a button?
    }

    @Override
    protected void processMouseButtonControls(MouseButtonControl mbc) {
        //TODO discuss should this result in 1 vs 0 from a button?
    }

    @Override
    protected void processGamepadAxisControls(GamepadAxisControl gpac) {
        amount = gpac.getAmount();
    }

    public float getAmount() {
        return amount;
    }
}
