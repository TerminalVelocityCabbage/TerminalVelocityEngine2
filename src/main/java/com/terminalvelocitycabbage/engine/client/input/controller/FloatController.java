package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;
import com.terminalvelocitycabbage.engine.client.input.types.MultiInputResolutionStrategy;

/**
 * An {@link Controller} useful for input types that are representative of a single 1 to 0 value, like a trigger pull
 * amount or a single joystick axis.
 */
public non-sealed abstract class FloatController extends Controller {

    MultiInputResolutionStrategy strategy;
    float amount;

    /**
     * @param controls A list of {@link Control}s that are processed into a 1 to 0 float value
     */
    public FloatController(Control... controls) {
        this(MultiInputResolutionStrategy.FLOAT_MAX, controls);
    }

    /**
     * @param mirs An {@link MultiInputResolutionStrategy} that determines how multiple input devices being used at the
     *             same time's inputs shall be resolved. For example if a W key and forward on a joystick is mapped to
     *             this controller and both are forward and MultiInputResolutionStrategy.FLOAT_MAX is used the input with
     *             the higher float value will be used.
     * @param controls A list of {@link Control}s that are processed into a 1 to 0 float value
     */
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

    /**
     * @return A float value from 1 to 0 that represents this controller output.
     */
    public float getAmount() {
        return amount;
    }
}
