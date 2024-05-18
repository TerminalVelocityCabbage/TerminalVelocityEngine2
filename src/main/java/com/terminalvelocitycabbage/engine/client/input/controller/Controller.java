package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;

public abstract sealed class Controller permits BooleanController, FloatController, GroupedController2f, GroupedController4f, GroupedController6f {

    ControlGroup[] controlGroups;

    public abstract void act();

    public void preProcess() { }

    public void processInputs() {
        preProcess();
        int groupIndex = 0;
        for (ControlGroup controlGroup : controlGroups) {
            for (KeyboardKeyControl kkc : controlGroup.keyboardKeyControls) processKeyControlInput(kkc, groupIndex);
            for (GamepadButtonControl gpbc : controlGroup.gamepadButtonControls) processGamepadButtonControl(gpbc, groupIndex);
            for (MouseButtonControl mbc : controlGroup.mouseButtonControls) processMouseButtonControls(mbc, groupIndex);
            for (GamepadAxisControl gpac : controlGroup.gamepadAxisControls) processGamepadAxisControls(gpac, groupIndex);
            for (MouseMovementControl mmc: controlGroup.mouseMovementControls) processMouseMovementControls(mmc, groupIndex);
            groupIndex++;
        }
        postProcess();
        act();
        postAction();
    }

    public void postProcess() { }

    public void postAction() { }

    protected abstract void processKeyControlInput(KeyboardKeyControl kkc, int groupIndex);

    protected abstract void processGamepadButtonControl(GamepadButtonControl gbc, int groupIndex);

    protected abstract void processMouseButtonControls(MouseButtonControl mbc, int groupIndex);

    protected abstract void processGamepadAxisControls(GamepadAxisControl gpac, int groupIndex);

    protected abstract void processMouseMovementControls(MouseMovementControl mmc, int groupIndex);
}
