package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;
import com.terminalvelocitycabbage.engine.client.input.types.MultiInputResolutionStrategy;

/**
 * Similar to an {@link FloatController} but with 6 components.
 */
public abstract non-sealed class GroupedController6f extends Controller {

    MultiInputResolutionStrategy strategy;

    float forwardAmount;
    float backwardAmount;
    float leftAmount;
    float rightAmount;
    float upAmount;
    float downAmount;

    /**
     * @param forward An {@link ControlGroup} representing the first or "forward" component of this {@link Controller}
     * @param backward An {@link ControlGroup} representing the second or "backward" component of this {@link Controller}
     * @param left An {@link ControlGroup} representing the third or "left" component of this {@link Controller}
     * @param right An {@link ControlGroup} representing the fourth or "right" component of this {@link Controller}
     * @param up An {@link ControlGroup} representing the fifth or "up" component of this {@link Controller}
     * @param down An {@link ControlGroup} representing the sixth or "down" component of this {@link Controller}
     */
    public GroupedController6f(ControlGroup forward, ControlGroup backward, ControlGroup left, ControlGroup right, ControlGroup up, ControlGroup down) {
        this(MultiInputResolutionStrategy.FLOAT_MAX, forward, backward, left, right, up, down);
    }

    /**
     * @param strategy An {@link MultiInputResolutionStrategy} that determines how multiple input devices being used at the
     *                 same time's inputs shall be resolved. For example if a W key and forward on a joystick is mapped to
     *                 this controller and both are forward and MultiInputResolutionStrategy.FLOAT_MAX is used the input with
     *                 the higher float value will be used.
     * @param forward An {@link ControlGroup} representing the first or "forward" component of this {@link Controller}
     * @param backward An {@link ControlGroup} representing the second or "backward" component of this {@link Controller}
     * @param left An {@link ControlGroup} representing the third or "left" component of this {@link Controller}
     * @param right An {@link ControlGroup} representing the fourth or "right" component of this {@link Controller}
     * @param up An {@link ControlGroup} representing the fifth or "up" component of this {@link Controller}
     * @param down An {@link ControlGroup} representing the sixth or "down" component of this {@link Controller}
     */
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

    @Override
    protected void processMouseMovementControls(MouseMovementControl mmc, int groupIndex) {
        if (groupIndex == 0) forwardAmount = strategy.resolve(forwardAmount, mmc.getAmount());
        if (groupIndex == 1) backwardAmount = strategy.resolve(backwardAmount, mmc.getAmount());
        if (groupIndex == 2) leftAmount = strategy.resolve(leftAmount, mmc.getAmount());
        if (groupIndex == 3) rightAmount = strategy.resolve(rightAmount, mmc.getAmount());
        if (groupIndex == 4) upAmount = strategy.resolve(upAmount, mmc.getAmount());
        if (groupIndex == 5) downAmount = strategy.resolve(downAmount, mmc.getAmount());
    }

    @Override
    protected void processMouseScrollControls(MouseScrollControl msc, int groupIndex) {
        if (groupIndex == 0) forwardAmount = strategy.resolve(forwardAmount, msc.getAmount());
        if (groupIndex == 1) backwardAmount = strategy.resolve(backwardAmount, msc.getAmount());
        if (groupIndex == 2) leftAmount = strategy.resolve(leftAmount, msc.getAmount());
        if (groupIndex == 3) rightAmount = strategy.resolve(rightAmount, msc.getAmount());
        if (groupIndex == 4) upAmount = strategy.resolve(upAmount, msc.getAmount());
        if (groupIndex == 5) downAmount = strategy.resolve(downAmount, msc.getAmount());
    }

    /**
     * @return Get the first component of this Controller
     */
    public float getForwardAmount() {
        return forwardAmount;
    }

    /**
     * @return Get the second component of this Controller
     */
    public float getBackwardAmount() {
        return backwardAmount;
    }

    /**
     * @return Get the third component of this Controller
     */
    public float getLeftAmount() {
        return leftAmount;
    }

    /**
     * @return Get the fourth component of this Controller
     */
    public float getRightAmount() {
        return rightAmount;
    }

    /**
     * @return Get the fifth component of this Controller
     */
    public float getUpAmount() {
        return upAmount;
    }

    /**
     * @return Get the sixth component of this Controller
     */
    public float getDownAmount() {
        return downAmount;
    }
}
