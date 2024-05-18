package com.terminalvelocitycabbage.engine.client.input.controller;

import com.terminalvelocitycabbage.engine.client.input.control.*;

import java.util.ArrayList;
import java.util.List;

public class ControlGroup {

    protected final List<KeyboardKeyControl> keyboardKeyControls = new ArrayList<>();
    protected final List<GamepadButtonControl> gamepadButtonControls = new ArrayList<>();
    protected final List<MouseButtonControl> mouseButtonControls = new ArrayList<>();
    protected final List<GamepadAxisControl> gamepadAxisControls = new ArrayList<>();
    protected final List<MouseMovementControl> mouseMovementControls = new ArrayList<>();
    protected final List<MouseScrollControl> mouseScrollControls = new ArrayList<>();

    public ControlGroup(Control... controls) {
        for (Control control : controls) {
            switch (control) {
                case KeyboardKeyControl kkc -> keyboardKeyControls.add(kkc);
                case GamepadButtonControl gpbc -> gamepadButtonControls.add(gpbc);
                case MouseButtonControl mbc -> mouseButtonControls.add(mbc);
                case GamepadAxisControl gpac -> gamepadAxisControls.add(gpac);
                case MouseMovementControl mmc -> mouseMovementControls.add(mmc);
                case MouseScrollControl msc -> mouseScrollControls.add(msc);
            }
        }
    }

}
