package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;

/**
 * A Controller is a game context representation derived from a set of raw inputs or {@link ControlGroup}s
 * This is usually defined by multiple sources of input that results in at least one output from 0 to 1.
 */
public abstract sealed class Controller permits BooleanController, FloatController, GroupedController2f, GroupedController4f, GroupedController6f {

    ControlGroup[] controlGroups;

    /**
     * Defines what this controller is meant to do when certain conditions are met. Implemented by the game.
     */
    public abstract void act();

    /**
     * Any clerical processes that need to be completed at the beginning of the input mapping. Usually implemented by
     * one of the permitted classes of this sealed class.
     */
    public void preProcess() { }

    /**
     * Queries the current raw inputs to this controller for interpretation each input frame called by the engine
     * automatically, usually should not be overriden by the user.
     */
    public void processInputs() {
        preProcess();
        int groupIndex = 0;
        for (ControlGroup controlGroup : controlGroups) {
            for (KeyboardKeyControl kkc : controlGroup.keyboardKeyControls) processKeyControlInput(kkc, groupIndex);
            for (GamepadButtonControl gpbc : controlGroup.gamepadButtonControls) processGamepadButtonControl(gpbc, groupIndex);
            for (MouseButtonControl mbc : controlGroup.mouseButtonControls) processMouseButtonControls(mbc, groupIndex);
            for (GamepadAxisControl gpac : controlGroup.gamepadAxisControls) processGamepadAxisControls(gpac, groupIndex);
            for (MouseMovementControl mmc: controlGroup.mouseMovementControls) processMouseMovementControls(mmc, groupIndex);
            for (MouseScrollControl msc: controlGroup.mouseScrollControls) processMouseScrollControls(msc, groupIndex);
            groupIndex++;
        }
        postProcess();
        act();
        postAction();
    }

    /**
     * Any clerical processes that need to be done post input processing BUT BEFORE the action.
     */
    public void postProcess() { }

    /**
     * Any clerical processes that need to be done post action.
     */
    public void postAction() { }

    /**
     * A method implemented by the permitted classes to map control types to legible values by the controller. Specifically
     * for keyboard key controls.
     * @param kkc The {@link KeyboardKeyControl} that this input process method is gathering information from to map to a useful value.
     * @param groupIndex An index value for use in multi-group control types like an {@link GroupedController6f} to
     *                   access each of the groups for individual mapping
     */
    protected abstract void processKeyControlInput(KeyboardKeyControl kkc, int groupIndex);

    /**
     * A method implemented by the permitted classes to map control types to legible values by the controller. Specifically
     * for gamepad button controls.
     * @param gbc The {@link GamepadButtonControl} that this input process method is gathering information from to map to a useful value.
     * @param groupIndex An index value for use in multi-group control types like an {@link GroupedController6f} to
     *                   access each of the groups for individual mapping
     */
    protected abstract void processGamepadButtonControl(GamepadButtonControl gbc, int groupIndex);

    /**
     * A method implemented by the permitted classes to map control types to legible values by the controller. Specifically
     * for mouse button controls.
     * @param mbc The {@link MouseButtonControl} that this input process method is gathering information from to map to a useful value.
     * @param groupIndex An index value for use in multi-group control types like an {@link GroupedController6f} to
     *                   access each of the groups for individual mapping
     */
    protected abstract void processMouseButtonControls(MouseButtonControl mbc, int groupIndex);

    /**
     * A method implemented by the permitted classes to map control types to legible values by the controller. Specifically
     * for mouse button controls.
     * @param gpac The {@link GamepadAxisControl} that this input process method is gathering information from to map to a useful value.
     * @param groupIndex An index value for use in multi-group control types like an {@link GroupedController6f} to
     *                   access each of the groups for individual mapping
     */
    protected abstract void processGamepadAxisControls(GamepadAxisControl gpac, int groupIndex);

    /**
     * A method implemented by the permitted classes to map control types to legible values by the controller. Specifically
     * for mouse movement controls.
     * @param mmc The {@link MouseMovementControl} that this input process method is gathering information from to map to a useful value.
     * @param groupIndex An index value for use in multi-group control types like an {@link GroupedController6f} to
     *                   access each of the groups for individual mapping
     */
    protected abstract void processMouseMovementControls(MouseMovementControl mmc, int groupIndex);

    /**
     * A method implemented by the permitted classes to map control types to legible values by the controller. Specifically
     * for mouse scroll controls.
     * @param msc The {@link MouseScrollControl} that this input process method is gathering information from to map to a useful value.
     * @param groupIndex An index value for use in multi-group control types like an {@link GroupedController6f} to
     *                   access each of the groups for individual mapping
     */
    protected abstract void processMouseScrollControls(MouseScrollControl msc, int groupIndex);
}
