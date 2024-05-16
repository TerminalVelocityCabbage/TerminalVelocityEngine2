package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;

import java.util.ArrayList;
import java.util.List;

public abstract sealed class Controller permits FloatController, GroupedController2f, BooleanController {

    final List<KeyboardKeyControl> keyboardKeyControls = new ArrayList<>();
    final List<GamepadButtonControl> gamepadButtonControls = new ArrayList<>();
    final List<MouseButtonControl> mouseButtonControls = new ArrayList<>();
    final List<GamepadAxisControl> gamepadAxisControls = new ArrayList<>();

    public Controller(Control... controls) {
        for (Control control : controls) {
            switch (control) {
                case KeyboardKeyControl kkc -> keyboardKeyControls.add(kkc);
                case GamepadButtonControl gpbc -> gamepadButtonControls.add(gpbc);
                case MouseButtonControl mbc -> mouseButtonControls.add(mbc);
                case GamepadAxisControl gpac -> gamepadAxisControls.add(gpac);
            }
        }
    }

    public abstract void act();

    public void preProcess() { }

    public void processInputs() {
        preProcess();
        for (KeyboardKeyControl kkc : keyboardKeyControls) processKeyControlInput(kkc);
        for (GamepadButtonControl gpbc : gamepadButtonControls) processGamepadButtonControl(gpbc);
        for (MouseButtonControl mbc : mouseButtonControls) processMouseButtonControls(mbc);
        for (GamepadAxisControl gpac : gamepadAxisControls) processGamepadAxisControls(gpac);
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
