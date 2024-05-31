package com.terminalvelocitycabbage.engine.client.window;

import com.terminalvelocitycabbage.engine.client.ClientBase;

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

    float mouseScrollYDelta;
    float mouseScrollXDelta;

    /**
     * This should not be called by the user
     * Clears the input callback queue so that a new frame can be captured in the next input tick
     */
    public void reset() {
        mouseUpwardDelta = 0;
        mouseDownwardDelta = 0;
        mouseLeftwardDelta = 0;
        mouseRightwardDelta = 0;
        mouseScrollYDelta = 0;
        mouseScrollXDelta = 0;
    }

    //TODO this will be used for things like chat windows where we just need to get a queue of characters
    protected void charCallback(long window, int character) {
//        Log.info("Character Input: " +
//                ClientBase.getInstance().getWindowManager().getPropertiesFromWindow(window).getTitle() +
//                " " + String.copyValueOf(Character.toChars(character)));
    }

    /**
     * @param window The window which this callback belongs to
     * @param x The x position of the mouse
     * @param y The y position of the mouse
     */
    protected void cursorPosCallback(long window, double x, double y) {

        //IF this is the first time this callback has run since the program started skip the current position and just set the last positions
        if (lastMouseY == -1 && lastMouseX == -1) {
            lastMouseX = x;
            lastMouseY = y;
            return;
        }

        //Get the properties from this window
        WindowProperties properties = ClientBase.getInstance().getWindowManager().getPropertiesFromWindow(window);

        //Split the y movement of the mouse up into 2 components for up and down movement and update the deltas
        if (lastMouseY < y) {
            mouseDownwardDelta += Math.abs((float) ((y - lastMouseY) / properties.getHeight()));
        } else {
            mouseUpwardDelta += Math.abs((float) ((y - lastMouseY) / properties.getHeight()));
        }

        //Split the x movement of the mouse into 2 components left and right and update the deltas
        if (lastMouseX < x) {
            mouseLeftwardDelta += Math.abs((float) ((x - lastMouseX) / properties.getWidth()));
        } else {
            mouseRightwardDelta += Math.abs((float) ((x - lastMouseX) / properties.getWidth()));
        }

        //Set what will be the previous mouse position to the current position
        lastMouseY = y;
        lastMouseX = x;
    }

    /**
     * @param window The window that this callback is called from
     * @param deltaX The amount of scrolling that happened in the x direction
     * @param deltaY The amount of scrolling that happened in the x direction
     */
    protected void scrollCallback(long window, double deltaX, double deltaY) {
        mouseScrollYDelta += (float) deltaY;
        mouseScrollXDelta += (float) deltaX;
    }

    /**
     * @return The float from 0 to 1 that defined the change in movement of the mouse this input frame upwards
     */
    public float getMouseUpwardDelta() {
        return mouseUpwardDelta;
    }

    /**
     * @return The float from 0 to 1 that defined the change in movement of the mouse this input frame downwards
     */
    public float getMouseDownwardDelta() {
        return mouseDownwardDelta;
    }

    /**
     * @return The float from 0 to 1 that defined the change in movement of the mouse this input frame leftwards
     */
    public float getMouseLeftwardDelta() {
        return mouseLeftwardDelta;
    }

    /**
     * @return The float from 0 to 1 that defined the change in movement of the mouse this input frame rightwards
     */
    public float getMouseRightwardDelta() {
        return mouseRightwardDelta;
    }

    /**
     * @return The float from 0 to infinity that defines the change in position of the mouse scroll wheel upwards
     */
    public float getMouseScrollUpDelta() {
        return Math.max(0, mouseScrollYDelta);
    }

    /**
     * @return The float from 0 to infinity that defines the change in position of the mouse scroll wheel downwards
     */
    public float getMouseScrollDownDelta() {
        return Math.abs(Math.min(0, mouseScrollYDelta));
    }

    /**
     * @return The float from 0 to infinity that defines the change in position of the mouse scroll wheel leftwards
     */
    public float getMouseScrollLeftDelta() {
        return Math.abs(Math.min(0, mouseScrollXDelta));
    }

    /**
     * @return The float from 0 to infinity that defines the change in position of the mouse scroll wheel rightwards
     */
    public float getMouseScrollRightDelta() {
        return Math.max(0, mouseScrollXDelta);
    }
}
