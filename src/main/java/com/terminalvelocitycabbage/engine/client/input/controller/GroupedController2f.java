package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;
import com.terminalvelocitycabbage.engine.client.input.types.MultiInputResolutionStrategy;

public abstract non-sealed class GroupedController2f extends Controller {

    MultiInputResolutionStrategy strategy;

    float positiveAmount;
    float negativeAmount;

    public GroupedController2f(ControlGroup positive, ControlGroup negative) {
        this(MultiInputResolutionStrategy.FLOAT_MAX, positive, negative);
    }

    public GroupedController2f(MultiInputResolutionStrategy strategy, ControlGroup positive, ControlGroup negative) {
        this.strategy = strategy;
        controlGroups = new ControlGroup[]{positive, negative};
    }

    @Override
    public void postAction() {
        positiveAmount = 0;
        negativeAmount = 0;
    }

    @Override
    protected void processKeyControlInput(KeyboardKeyControl kkc, int groupIndex) {
        if (groupIndex == 0) positiveAmount = strategy.resolve(positiveAmount, kkc.getAmount());
        if (groupIndex == 1) negativeAmount = strategy.resolve(negativeAmount, kkc.getAmount());
    }

    @Override
    protected void processGamepadButtonControl(GamepadButtonControl gbc, int groupIndex) {
        if (groupIndex == 0) positiveAmount = strategy.resolve(positiveAmount, gbc.getAmount());
        if (groupIndex == 1) negativeAmount = strategy.resolve(negativeAmount, gbc.getAmount());
    }

    @Override
    protected void processMouseButtonControls(MouseButtonControl mbc, int groupIndex) {
        if (groupIndex == 0) positiveAmount = strategy.resolve(positiveAmount, mbc.getAmount());
        if (groupIndex == 1) negativeAmount = strategy.resolve(negativeAmount, mbc.getAmount());
    }

    @Override
    protected void processGamepadAxisControls(GamepadAxisControl gpac, int groupIndex) {
        if (groupIndex == 0) positiveAmount = strategy.resolve(positiveAmount, gpac.getAmount());
        if (groupIndex == 1) negativeAmount = strategy.resolve(negativeAmount, gpac.getAmount());
    }

    @Override
    protected void processMouseMovementControls(MouseMovementControl mmc, int groupIndex) {
        if (groupIndex == 0) positiveAmount = strategy.resolve(positiveAmount, mmc.getAmount());
        if (groupIndex == 1) negativeAmount = strategy.resolve(negativeAmount, mmc.getAmount());
    }

    @Override
    protected void processMouseScrollControls(MouseScrollControl msc, int groupIndex) {
        if (groupIndex == 0) positiveAmount = strategy.resolve(positiveAmount, msc.getAmount());
        if (groupIndex == 1) negativeAmount = strategy.resolve(negativeAmount, msc.getAmount());
    }

    public float getPositiveAmount() {
        return positiveAmount;
    }

    public float getNegativeAmount() {
        return negativeAmount;
    }

    public float getSummedAmount() {
        return positiveAmount - negativeAmount;
    }
}
