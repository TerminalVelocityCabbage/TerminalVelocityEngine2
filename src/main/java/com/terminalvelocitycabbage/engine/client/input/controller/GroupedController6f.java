package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.GamepadAxisControl;
import com.terminalvelocitycabbage.engine.client.input.control.GamepadButtonControl;
import com.terminalvelocitycabbage.engine.client.input.control.KeyboardKeyControl;
import com.terminalvelocitycabbage.engine.client.input.control.MouseButtonControl;
import com.terminalvelocitycabbage.engine.client.input.types.MultiInputResolutionStrategy;

public abstract non-sealed class GroupedController6f extends Controller {

    MultiInputResolutionStrategy strategy;

    float forwardAmount;
    float backwardAmount;
    float leftAmount;
    float rightAmount;
    float upAmount;
    float downAmount;

    public GroupedController6f(ControlGroup forward, ControlGroup backward, ControlGroup left, ControlGroup right, ControlGroup up, ControlGroup down) {
        this(MultiInputResolutionStrategy.FLOAT_MAX, forward, backward, left, right, up, down);
    }

    public GroupedController6f(MultiInputResolutionStrategy strategy, ControlGroup forward, ControlGroup backward, ControlGroup left, ControlGroup right, ControlGroup up, ControlGroup down) {
        this.strategy = strategy;
        controlGroups = new ControlGroup[]{forward, backward, left, right, up, down};
    }

    @Override
    public void postAction() {
        forwardAmount = 0;
        backwardAmount = 0;
        leftAmount = 0;
        rightAmount = 0;
        upAmount = 0;
        downAmount = 0;
    }

    @Override
    protected void processKeyControlInput(KeyboardKeyControl kkc, int groupIndex) {
        if (groupIndex == 0) forwardAmount = strategy.resolve(forwardAmount, kkc.getAmount());
        if (groupIndex == 1) backwardAmount = strategy.resolve(backwardAmount, kkc.getAmount());
        if (groupIndex == 2) leftAmount = strategy.resolve(leftAmount, kkc.getAmount());
        if (groupIndex == 3) rightAmount = strategy.resolve(rightAmount, kkc.getAmount());
        if (groupIndex == 4) upAmount = strategy.resolve(upAmount, kkc.getAmount());
        if (groupIndex == 5) downAmount = strategy.resolve(downAmount, kkc.getAmount());
    }

    @Override
    protected void processGamepadButtonControl(GamepadButtonControl gbc, int groupIndex) {
        if (groupIndex == 0) forwardAmount = strategy.resolve(forwardAmount, gbc.getAmount());
        if (groupIndex == 1) backwardAmount = strategy.resolve(backwardAmount, gbc.getAmount());
        if (groupIndex == 2) leftAmount = strategy.resolve(leftAmount, gbc.getAmount());
        if (groupIndex == 3) rightAmount = strategy.resolve(rightAmount, gbc.getAmount());
        if (groupIndex == 4) upAmount = strategy.resolve(upAmount, gbc.getAmount());
        if (groupIndex == 5) downAmount = strategy.resolve(downAmount, gbc.getAmount());
    }

    @Override
    protected void processMouseButtonControls(MouseButtonControl mbc, int groupIndex) {
        if (groupIndex == 0) forwardAmount = strategy.resolve(forwardAmount, mbc.getAmount());
        if (groupIndex == 1) backwardAmount = strategy.resolve(backwardAmount, mbc.getAmount());
        if (groupIndex == 2) leftAmount = strategy.resolve(leftAmount, mbc.getAmount());
        if (groupIndex == 3) rightAmount = strategy.resolve(rightAmount, mbc.getAmount());
        if (groupIndex == 4) upAmount = strategy.resolve(upAmount, mbc.getAmount());
        if (groupIndex == 5) downAmount = strategy.resolve(downAmount, mbc.getAmount());
    }

    @Override
    protected void processGamepadAxisControls(GamepadAxisControl gpac, int groupIndex) {
        if (groupIndex == 0) forwardAmount = strategy.resolve(forwardAmount, gpac.getAmount());
        if (groupIndex == 1) backwardAmount = strategy.resolve(backwardAmount, gpac.getAmount());
        if (groupIndex == 2) leftAmount = strategy.resolve(leftAmount, gpac.getAmount());
        if (groupIndex == 3) rightAmount = strategy.resolve(rightAmount, gpac.getAmount());
        if (groupIndex == 4) upAmount = strategy.resolve(upAmount, gpac.getAmount());
        if (groupIndex == 5) downAmount = strategy.resolve(downAmount, gpac.getAmount());
    }

    public float getForwardAmount() {
        return forwardAmount;
    }

    public float getBackwardAmount() {
        return backwardAmount;
    }

    public float getLeftAmount() {
        return leftAmount;
    }

    public float getRightAmount() {
        return rightAmount;
    }

    public float getUpAmount() {
        return upAmount;
    }

    public float getDownAmount() {
        return downAmount;
    }
}
