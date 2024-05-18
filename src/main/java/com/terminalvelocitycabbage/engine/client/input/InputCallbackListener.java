package com.terminalvelocitycabbage.engine.client.input;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.debug.Log;

/**
 * A class that converts raw glfw input callback events that cannot be queried by an instant into useful
 * data to be queried by the input handler
 */
public class InputCallbackListener {

    float mouseUpwardDelta;
    float mouseDownwardDelta;
    float mouseLeftwardDelta;
    float mouseRightwardDelta;

    double lastMouseY = -1;
    double lastMouseX = -1;

    public void reset() {
        mouseUpwardDelta = 0;
        mouseDownwardDelta = 0;
        mouseLeftwardDelta = 0;
        mouseRightwardDelta = 0;
    }

    public void charCallback(long window, int character) {
//        Log.info("Character Input: " +
//                ClientBase.getInstance().getWindowManager().getPropertiesFromWindow(window).getTitle() +
//                " " + String.copyValueOf(Character.toChars(character)));
    }

    public void cursorPosCallback(long window, double x, double y) {

        if (lastMouseY == -1 && lastMouseX == -1) {
            lastMouseX = x;
            lastMouseY = y;
        }

        WindowProperties properties = ClientBase.getInstance().getWindowManager().getPropertiesFromWindow(window);

        if (lastMouseY < y) {
            mouseDownwardDelta += Math.abs((float) ((y - lastMouseY) / properties.getHeight()));
        } else {
            mouseUpwardDelta += Math.abs((float) ((y - lastMouseY) / properties.getHeight()));
        }

        if (lastMouseX < x) {
            mouseLeftwardDelta += Math.abs((float) ((x - lastMouseX) / properties.getWidth()));
        } else {
            mouseRightwardDelta += Math.abs((float) ((x - lastMouseX) / properties.getWidth()));
        }

        lastMouseY = y;
        lastMouseX = x;
    }

    //TODO note that this is called more than once per frame often times, so track more than one update
    public void scrollCallback(long window, double deltaX, double deltaY) {
        Log.info("Scrolling: " +
                ClientBase.getInstance().getWindowManager().getPropertiesFromWindow(window).getTitle() +
                " " + deltaX +
                ", " + deltaY);
    }

    public float getMouseUpwardDelta() {
        return mouseUpwardDelta;
    }

    public float getMouseDownwardDelta() {
        return mouseDownwardDelta;
    }

    public float getMouseLeftwardDelta() {
        return mouseLeftwardDelta;
    }

    public float getMouseRightwardDelta() {
        return mouseRightwardDelta;
    }
}
