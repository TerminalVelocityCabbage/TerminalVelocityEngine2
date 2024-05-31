package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;
import com.terminalvelocitycabbage.engine.client.input.types.MultiInputResolutionStrategy;

/**
 * Similar to an {@link FloatController} but with 2 components.
 */
public abstract non-sealed class GroupedController2f extends Controller {

    MultiInputResolutionStrategy strategy;

    float positiveAmount;
    float negativeAmount;

    /**
     * @param positive An {@link ControlGroup} representing the first or "positive" component of this {@link Controller}
     * @param negative An {@link ControlGroup} representing the second or "negative" component of this {@link Controller}
     */
    public GroupedController2f(ControlGroup positive, ControlGroup negative) {
        this(MultiInputResolutionStrategy.FLOAT_MAX, positive, negative);
    }

    /**
     * @param strategy An {@link MultiInputResolutionStrategy} that determines how multiple input devices being used at the
     *                 same time's inputs shall be resolved. For example if a W key and forward on a joystick is mapped to
     *                 this controller and both are forward and MultiInputResolutionStrategy.FLOAT_MAX is used the input with
     *                 the higher float value will be used.
     * @param positive An {@link ControlGroup} representing the first or "positive" component of this {@link Controller}
     * @param negative An {@link ControlGroup} representing the second or "negative" component of this {@link Controller}
     */
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

    /**
     * @return Get the first or positive component float value
     */
    public float getPositiveAmount() {
        return positiveAmount;
    }

    /**
     * @return Get the second or negative component float value
     */
    public float getNegativeAmount() {
        return negativeAmount;
    }

    /**
     * @return Get the first and second components summed together amount
     */
    public float getSummedAmount() {
        return positiveAmount - negativeAmount;
    }
}
