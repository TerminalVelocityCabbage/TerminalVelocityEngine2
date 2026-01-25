package com.terminalvelocitycabbage.engine.client.ui;

import org.joml.Vector2f;

public class UIInputState {

    private final Vector2f mousePosition = new Vector2f();
    private boolean leftMouseClicked;
    private boolean leftMouseDown;
    private final Vector2f scrollDelta = new Vector2f();

    public UIInputState() {
    }

    public void copyFrom(UIInputState other) {
        synchronized (other) {
            this.mousePosition.set(other.mousePosition);
            this.leftMouseClicked = other.leftMouseClicked;
            this.leftMouseDown = other.leftMouseDown;
            this.scrollDelta.set(other.scrollDelta);
        }
    }

    public void resetOneTimeState() {
        leftMouseClicked = false;
        scrollDelta.set(0, 0);
    }

    public Vector2f getMousePosition() {
        return mousePosition;
    }

    public boolean isLeftMouseClicked() {
        return leftMouseClicked;
    }

    public void setLeftMouseClicked(boolean leftMouseClicked) {
        this.leftMouseClicked = leftMouseClicked;
    }

    public boolean isLeftMouseDown() {
        return leftMouseDown;
    }

    public void setLeftMouseDown(boolean leftMouseDown) {
        this.leftMouseDown = leftMouseDown;
    }

    public Vector2f getScrollDelta() {
        return scrollDelta;
    }
}
