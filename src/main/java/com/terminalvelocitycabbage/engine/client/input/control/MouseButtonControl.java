package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.MouseInput;
import com.terminalvelocitycabbage.engine.client.input.util.MouseInputUtil;

public non-sealed class MouseButtonControl extends ButtonControl {

    MouseInput.Button button;

    public MouseButtonControl(MouseInput.Button button) {
        this.button = button;
    }

    @Override
    public boolean isPressed(InputHandler inputHandler) {
        return MouseInputUtil.isMouseButtonPressed(inputHandler.getMousedOverWindow(), button);
    }

    public MouseInput.Button getButton() {
        return button;
    }
}
