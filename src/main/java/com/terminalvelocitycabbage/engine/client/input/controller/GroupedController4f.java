package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;
import com.terminalvelocitycabbage.engine.client.input.types.MultiInputResolutionStrategy;

public abstract non-sealed class GroupedController4f extends Controller {

    MultiInputResolutionStrategy strategy;

    float forwardAmount;
    float backwardAmount;
    float leftAmount;
    float rightAmount;

    public GroupedController4f(ControlGroup forward, ControlGroup backward, ControlGroup left, ControlGroup right) {
        this(MultiInputResolutionStrategy.FLOAT_MAX, forward, backward, left, right);
    }

    public GroupedController4f(MultiInputResolutionStrategy strategy, ControlGroup forward, ControlGroup backward, ControlGroup left, ControlGroup right) {
        this.strategy = strategy;
        controlGroups = new ControlGroup[]{forward, backward, left, right};
    }

    @Override
    public void postAction() {
        forwardAmount = 0;
        backwardAmount = 0;
        leftAmount = 0;
        rightAmount = 0;
    }

    @Override
    protected void processKeyControlInput(KeyboardKeyControl kkc, int groupIndex) {
        if (groupIndex == 0) forwardAmount = strategy.resolve(forwardAmount, kkc.getAmount());
        if (groupIndex == 1) backwardAmount = strategy.resolve(backwardAmount, kkc.getAmount());
        if (groupIndex == 2) leftAmount = strategy.resolve(leftAmount, kkc.getAmount());
        if (groupIndex == 3) rightAmount = strategy.resolve(rightAmount, kkc.getAmount());
    }

    @Override
    protected void processGamepadButtonControl(GamepadButtonControl gbc, int groupIndex) {
        if (groupIndex == 0) forwardAmount = strategy.resolve(forwardAmount, gbc.getAmount());
        if (groupIndex == 1) backwardAmount = strategy.resolve(backwardAmount, gbc.getAmount());
        if (groupIndex == 2) leftAmount = strategy.resolve(leftAmount, gbc.getAmount());
        if (groupIndex == 3) rightAmount = strategy.resolve(rightAmount, gbc.getAmount());
    }

    @Override
    protected void processMouseButtonControls(MouseButtonControl mbc, int groupIndex) {
        if (groupIndex == 0) forwardAmount = strategy.resolve(forwardAmount, mbc.getAmount());
        if (groupIndex == 1) backwardAmount = strategy.resolve(backwardAmount, mbc.getAmount());
        if (groupIndex == 2) leftAmount = strategy.resolve(leftAmount, mbc.getAmount());
        if (groupIndex == 3) rightAmount = strategy.resolve(rightAmount, mbc.getAmount());
    }

    @Override
    protected void processGamepadAxisControls(GamepadAxisControl gpac, int groupIndex) {
        if (groupIndex == 0) forwardAmount = strategy.resolve(forwardAmount, gpac.getAmount());
        if (groupIndex == 1) backwardAmount = strategy.resolve(backwardAmount, gpac.getAmount());
        if (groupIndex == 2) leftAmount = strategy.resolve(leftAmount, gpac.getAmount());
        if (groupIndex == 3) rightAmount = strategy.resolve(rightAmount, gpac.getAmount());
    }

    @Override
    protected void processMouseMovementControls(MouseMovementControl mmc, int groupIndex) {
        if (groupIndex == 0) forwardAmount = strategy.resolve(forwardAmount, mmc.getAmount());
        if (groupIndex == 1) backwardAmount = strategy.resolve(backwardAmount, mmc.getAmount());
        if (groupIndex == 2) leftAmount = strategy.resolve(leftAmount, mmc.getAmount());
        if (groupIndex == 3) rightAmount = strategy.resolve(rightAmount, mmc.getAmount());
    }

    public float getForwardAmount() {
        return forwardAmount;
    }

    public float getUpwardAmount() {
        return getForwardAmount();
    }

    public float getBackwardAmount() {
        return backwardAmount;
    }

    public float getDownwardAmount() {
        return getBackwardAmount();
    }

    public float getLeftAmount() {
        return leftAmount;
    }

    public float getRightAmount() {
        return rightAmount;
    }
}
