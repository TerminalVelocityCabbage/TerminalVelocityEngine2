package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.MouseInput;
import com.terminalvelocitycabbage.engine.client.input.util.MouseInputUtil;

public non-sealed class MouseScrollControl extends Control {

    MouseInput.ScrollDirection direction;

    public MouseScrollControl(final MouseInput.ScrollDirection direction, float sensitivity) {
        super(sensitivity);
        this.direction = direction;
    }

    @Override
    public void update(InputHandler inputHandler, long deltaTime) {
        amount = MouseInputUtil.getScrollAmount(direction);
    }

    public MouseInput.ScrollDirection getDirection() {
        return direction;
    }

    public boolean hasScrolled() {
        return amount > 0;
    }
}
