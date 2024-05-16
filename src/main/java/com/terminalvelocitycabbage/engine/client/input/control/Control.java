package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;

public sealed abstract class Control permits GamepadButtonControl, KeyboardKeyControl {

    public abstract void update(InputHandler inputHandler, long deltaTime);

}
