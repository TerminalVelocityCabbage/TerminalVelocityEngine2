package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.Control;
import com.terminalvelocitycabbage.engine.client.input.control.GamepadButtonControl;
import com.terminalvelocitycabbage.engine.client.input.control.KeyboardKeyControl;

import java.util.ArrayList;
import java.util.List;

public abstract class Controller {

    final List<KeyboardKeyControl> keyboardKeyControls = new ArrayList<>();
    final List<GamepadButtonControl> gamepadButtonControls = new ArrayList<>();

    public Controller(Control... controls) {
        for (Control control : controls) {
            switch (control) {
                case KeyboardKeyControl kkc -> keyboardKeyControls.add(kkc);
                case GamepadButtonControl gpbc -> gamepadButtonControls.add(gpbc);
            }
        }
    }

    public void processInputs() {
        for (KeyboardKeyControl kkc : keyboardKeyControls) processKeyControlInput(kkc);
        for (GamepadButtonControl gpbc : gamepadButtonControls) processGamepadButtonControl(gpbc);
    }

    protected abstract void processKeyControlInput(KeyboardKeyControl kkc);

    protected abstract void processGamepadButtonControl(GamepadButtonControl gbc);

}
