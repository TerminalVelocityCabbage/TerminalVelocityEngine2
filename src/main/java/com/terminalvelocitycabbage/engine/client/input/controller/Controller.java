package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;

public abstract sealed class Controller permits FloatController, GroupedController2f, BooleanController {

    ControlGroup[] controlGroups;

    public abstract void act();

    public void preProcess() { }

    public void processInputs() {
        preProcess();
        for (ControlGroup controlGroup : controlGroups) {
            for (KeyboardKeyControl kkc : controlGroup.keyboardKeyControls) processKeyControlInput(kkc);
            for (GamepadButtonControl gpbc : controlGroup.gamepadButtonControls) processGamepadButtonControl(gpbc);
            for (MouseButtonControl mbc : controlGroup.mouseButtonControls) processMouseButtonControls(mbc);
            for (GamepadAxisControl gpac : controlGroup.gamepadAxisControls) processGamepadAxisControls(gpac);
        }
        postProcess();
        act();
        postAction();
    }

    public void postProcess() { }

    public void postAction() { }

    protected abstract void processKeyControlInput(KeyboardKeyControl kkc);

    protected abstract void processGamepadButtonControl(GamepadButtonControl gbc);

    protected abstract void processMouseButtonControls(MouseButtonControl mbc);

    protected abstract void processGamepadAxisControls(GamepadAxisControl gpac);
}
