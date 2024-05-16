package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.MouseButtonInput;
import com.terminalvelocitycabbage.engine.client.input.util.MouseInputUtil;

public non-sealed class MouseButtonControl extends ButtonControl {

    MouseButtonInput button;

    public MouseButtonControl(MouseButtonInput button) {
        this.button = button;
    }

    @Override
    public boolean isPressed(InputHandler inputHandler) {
        return MouseInputUtil.isMouseButtonPressed(inputHandler.getMousedOverWindow(), button);
    }

    public MouseButtonInput getButton() {
        return button;
    }
}
