package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.MouseInput;
import com.terminalvelocitycabbage.engine.client.input.util.MouseInputUtil;

/**
 * A Control type which listens to input from mouse buttons (Right, left, 3, 4, etc.)
 */
public non-sealed class MouseButtonControl extends ButtonControl {

    //The Button that this Control listens to
    MouseInput.Button button;

    public MouseButtonControl(MouseInput.Button button) {
        this.button = button;
    }

    @Override
    public boolean isPressed(InputHandler inputHandler) {
        return MouseInputUtil.isMouseButtonPressed(inputHandler.getMousedOverWindow(), button);
    }

    /**
     * @return The Button that this Control listens to
     */
    public MouseInput.Button getButton() {
        return button;
    }
}
