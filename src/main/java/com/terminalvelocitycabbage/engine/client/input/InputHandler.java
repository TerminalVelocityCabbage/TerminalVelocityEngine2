package com.terminalvelocitycabbage.engine.client.input;

import com.terminalvelocitycabbage.engine.client.input.control.Control;
import com.terminalvelocitycabbage.engine.client.input.control.KeyboardKeyControl;
import com.terminalvelocitycabbage.engine.client.input.util.GamepadInputUtil;
import com.terminalvelocitycabbage.engine.client.input.util.KeyboardInputUtil;
import com.terminalvelocitycabbage.engine.client.input.util.MouseInputUtil;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Registry;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.glfw.GLFW.*;

//It may be better to use glfwGetKey and glfwGetCursorPos and friends as opposed to the key callbacks because the key
//callbacks are not timed with the rest of the engine. we can update the input handler exactly when we want to if we
//get the status of a list of listened to keys.

//The hard part of doing the above is for character input for chat windows and things like that. we want to listen to
//Everything. So I think that it might make the most sense to use a combination of both? Key callback seems usless
//for most things, except it might be useful when we want to listen for new keybinds in an ingame configuration. Like
//"press which key you want to be for walk" etc. We will need the key callback for that.

public class InputHandler {

    GLFWGamepadState gamepadState;
    long focusedWindow;
    long mousedOverWindow;

    Registry<Control> controlRegistry;

    public InputHandler() {
        this.gamepadState = new GLFWGamepadState(MemoryUtil.memAlloc(GLFWGamepadState.SIZEOF));
    }

    /**
     * Marks an input read state, this updates all queues for input
     *
     * @param focusedWindow1    The window which is selected
     * @param mousedOverWindow1 The window which the mouse is over (or -1 if none)
     * @param deltaTime
     */
    public void update(long focusedWindow1, long mousedOverWindow1, long deltaTime) {

        this.focusedWindow = focusedWindow1;
        this.mousedOverWindow = mousedOverWindow1;

        //Process normal mouse/keyboard inputs
        processMouseKeyboardInputs(deltaTime);

        //Loop through all potential joysticks
        //TODO replace this with a list of connected joysticks instead
        for (int i = 0; i <= GLFW_JOYSTICK_LAST; i++) {
            //If this joystick is not connected skip it
            if (!glfwJoystickPresent(i)) continue;
            //Is this joystick mapped to onw of the SDL mapped controllers?
            if (glfwJoystickIsGamepad(i)) {
                // Retrieve joystick axes data and update the current gamepadState for reading later
                glfwGetGamepadState(i, gamepadState);
                //In this section of the code we can process inputs from gamepads
                processGamepadInputs(deltaTime);
            } else {
                //TODO this is a large undertaking and will likely result in needing to add a switch of MANY joystick
                //mappings here to take raw button and axis inputs and map it to a useful xbox like controller scheme
                Log.error("Joysticks not implemented in TVE, only gamepads. TODO! " + glfwGetJoystickName(i) + " will not work.. Sorry for the inconvenience.");
            }
        }
    }

    private void processMouseKeyboardInputs(long deltaTime) {

        //Loop through all control type and do something with them
        for (Control control : controlRegistry.getRegistryContents().values()) {
            switch (control) {
                case KeyboardKeyControl keyboardKeyControl -> keyboardKeyControl.update(this, deltaTime);
            }
        }



        if (KeyboardInputUtil.isKeyPressed(focusedWindow, GLFW_KEY_W)) Log.info("W");
        //if (mousedOverWindow != -1) Log.info("On-Demand Cursor Pos: " + MouseInputUtil.getMousePosition(mousedOverWindow));
        if (MouseInputUtil.isMouseButtonPressed(focusedWindow, GLFW_MOUSE_BUTTON_LEFT)) Log.info("Click");
    }

    private void processGamepadInputs(long deltaTime) {
        if (GamepadInputUtil.isButtonAPressed(gamepadState)) Log.info("A");
    }

    public GLFWGamepadState getGamepadState() {
        return gamepadState;
    }

    public long getFocusedWindow() {
        return focusedWindow;
    }

    public long getMousedOverWindow() {
        return mousedOverWindow;
    }
}
