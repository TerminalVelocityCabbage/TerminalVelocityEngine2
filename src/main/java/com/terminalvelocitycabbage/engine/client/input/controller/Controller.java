package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.Control;
import com.terminalvelocitycabbage.engine.client.input.control.GamepadButtonControl;
import com.terminalvelocitycabbage.engine.client.input.control.KeyboardKeyControl;
import com.terminalvelocitycabbage.engine.client.input.control.MouseButtonControl;

import java.util.ArrayList;
import java.util.List;

public abstract sealed class Controller permits ToggleController {

    final List<KeyboardKeyControl> keyboardKeyControls = new ArrayList<>();
    final List<GamepadButtonControl> gamepadButtonControls = new ArrayList<>();
    final List<MouseButtonControl> mouseButtonControls = new ArrayList<>();

    public Controller(Control... controls) {
        for (Control control : controls) {
            switch (control) {
                case KeyboardKeyControl kkc -> keyboardKeyControls.add(kkc);
                case GamepadButtonControl gpbc -> gamepadButtonControls.add(gpbc);
                case MouseButtonControl mbc -> mouseButtonControls.add(mbc);
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
        postProcess();
        act();
        postAction();
    }

    public void postProcess() { }

    public void postAction() { }

    protected abstract void processKeyControlInput(KeyboardKeyControl kkc);

    protected abstract void processGamepadButtonControl(GamepadButtonControl gbc);

    protected abstract void processMouseButtonControls(MouseButtonControl mbc);

}
