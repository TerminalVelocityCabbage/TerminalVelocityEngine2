package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.Control;
import com.terminalvelocitycabbage.engine.client.input.control.KeyboardKeyControl;

import java.util.ArrayList;
import java.util.List;

public abstract class Controller {

    List<KeyboardKeyControl> keyboardKeyControls;

    public Controller(Control... controls) {

        keyboardKeyControls = new ArrayList<>();

        for (Control control : controls) {
            switch (control) {
                case KeyboardKeyControl kkc -> keyboardKeyControls.add(kkc);
            }
        }
    }

    public void processInputs() {
        for (KeyboardKeyControl kkc : keyboardKeyControls) processKeyControlInput(kkc);
    }

    protected abstract void processKeyControlInput(KeyboardKeyControl kkc);

}
