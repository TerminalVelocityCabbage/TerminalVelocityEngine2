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
        this.controlGroups = new ControlGroup[1];
        this.controlGroups[0] = new ControlGroup(controls);
        this.strategy = mirs;
    }

    @Override
    public void postAction() {
        amount = 0f;
    }

    @Override
    protected void processKeyControlInput(KeyboardKeyControl kkc, int groupIndex) {
        amount = strategy.resolve(amount, kkc.getAmount());
    }

    @Override
    protected void processGamepadButtonControl(GamepadButtonControl gbc, int groupIndex) {
        amount = strategy.resolve(amount, gbc.getAmount());
    }

    @Override
    protected void processMouseButtonControls(MouseButtonControl mbc, int groupIndex) {
        amount = strategy.resolve(amount, mbc.getAmount());
    }

    @Override
    protected void processGamepadAxisControls(GamepadAxisControl gpac, int groupIndex) {
        amount = strategy.resolve(amount, gpac.getAmount());
    }

    @Override
    protected void processMouseMovementControls(MouseMovementControl mmc, int groupIndex) {
        amount = strategy.resolve(amount, mmc.getAmount());
    }

    @Override
    protected void processMouseScrollControls(MouseScrollControl msc, int groupIndex) {
        amount = strategy.resolve(amount, msc.getAmount());
    }

    public float getAmount() {
        return amount;
    }
}
