package com.terminalvelocitycabbage.engine.client.input.control;

import com.terminalvelocitycabbage.engine.client.input.InputHandler;
import com.terminalvelocitycabbage.engine.client.input.types.MouseInput;
import com.terminalvelocitycabbage.engine.client.input.util.MouseInputUtil;

public non-sealed class MouseScrollControl extends Control {

    //The direction of scrolling that this Control listens to
    MouseInput.ScrollDirection direction;

    public MouseScrollControl(final MouseInput.ScrollDirection direction, float sensitivity) {
        super(sensitivity);
        this.direction = direction;
    }

    @Override
    public void update(InputHandler inputHandler, long deltaTime) {
        amount = MouseInputUtil.getScrollAmount(direction);
    }

    /**
     * @return The direction of scrolling that this Control listens to
     */
    public MouseInput.ScrollDirection getDirection() {
        return direction;
    }

    /**
     * @return Whether this direction of mouse scroll wheel has changed since the last frame
     */
    public boolean hasScrolled() {
        return amount > 0;
    }
}
