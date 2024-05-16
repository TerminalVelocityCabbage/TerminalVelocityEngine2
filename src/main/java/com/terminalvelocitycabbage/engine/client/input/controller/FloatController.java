package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;
import com.terminalvelocitycabbage.engine.client.input.types.MultiInputResolutionStrategy;

public non-sealed abstract class FloatController extends Controller {

    MultiInputResolutionStrategy strategy;
    float amount;

    public FloatController(Control... controls) {
        this(MultiInputResolutionStrategy.FLOAT_MAX, controls);
    }

    public FloatController(MultiInputResolutionStrategy mirs, Control... controls) {
        super(controls);
        this.strategy = mirs;
    }

    @Override
    public void postAction() {
        amount = 0f;
    }

    @Override
    protected void processKeyControlInput(KeyboardKeyControl kkc) {
        amount = strategy.resolve(amount, kkc.getAmount());
    }

    @Override
    protected void processGamepadButtonControl(GamepadButtonControl gbc) {
        amount = strategy.resolve(amount, gbc.getAmount());
    }

    @Override
    protected void processMouseButtonControls(MouseButtonControl mbc) {
        amount = strategy.resolve(amount, mbc.getAmount());
    }

    @Override
    protected void processGamepadAxisControls(GamepadAxisControl gpac) {
        amount = strategy.resolve(amount, gpac.getAmount());
    }

    public float getAmount() {
        return amount;
    }
}
