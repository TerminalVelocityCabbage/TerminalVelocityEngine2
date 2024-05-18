package com.terminalvelocitycabbage.engine.client.input;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.input.control.*;
import com.terminalvelocitycabbage.engine.client.input.controller.Controller;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
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
    Registry<Controller> controllerRegistry;

    public InputHandler() {
        this.gamepadState = new GLFWGamepadState(MemoryUtil.memAlloc(GLFWGamepadState.SIZEOF));
        this.controlRegistry = new Registry<>();
        this.controllerRegistry = new Registry<>();
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

        //Update controllers
        controllerRegistry.getRegistryContents().values().forEach(Controller::processInputs);
    }

    private void processMouseKeyboardInputs(long deltaTime) {

        //Loop through all control types and do something with them
        for (Control control : controlRegistry.getRegistryContents().values()) {
            switch (control) {
                case KeyboardKeyControl keyboardKeyControl -> keyboardKeyControl.update(this, deltaTime);
                case GamepadButtonControl gamepadButtonControl -> { }
                case MouseButtonControl mouseButtonControl -> mouseButtonControl.update(this, deltaTime);
                case GamepadAxisControl gamepadAxisControl -> { }
                case MouseMovementControl mouseMovementControl -> mouseMovementControl.update(this, deltaTime);
            }
        }
        //if (mousedOverWindow != -1) Log.info("On-Demand Cursor Pos: " + MouseInputUtil.getMousePosition(mousedOverWindow));
    }

    private void processGamepadInputs(long deltaTime) {
        //Loop through all control types and do something with them
        for (Control control : controlRegistry.getRegistryContents().values()) {
            switch (control) {
                case KeyboardKeyControl keyboardKeyControl -> { }
                case GamepadButtonControl gamepadButtonControl -> gamepadButtonControl.update(this, deltaTime);
                case MouseButtonControl mouseButtonControl -> { }
                case GamepadAxisControl gamepadAxisControl -> gamepadAxisControl.update(this, deltaTime);
                case MouseMovementControl mouseMovementControl -> { }
            }
        }
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

    public Control registerControlListener(Control control) {
        return switch (control) {
            case KeyboardKeyControl kkc -> controlRegistry.register(ClientBase.getInstance().identifierOf("keyboardKey_control_" + kkc.getKey().name()), kkc);
            case GamepadButtonControl gpbc -> controlRegistry.register(ClientBase.getInstance().identifierOf("gamepadButton_control_" + gpbc.getButton().name()), gpbc);
            case MouseButtonControl mbc -> controlRegistry.register(ClientBase.getInstance().identifierOf("mouseButton_control_" + mbc.getButton().name()), mbc);
            case GamepadAxisControl gpac -> controlRegistry.register(ClientBase.getInstance().identifierOf("gamepadAxis_control_" + gpac.getAxis().name()), gpac);
            case MouseMovementControl mmc -> controlRegistry.register(ClientBase.getInstance().identifierOf("mouseMovement_control_" + mmc.getAxis().name()), mmc);
        };
    }

    public Controller registerController(Identifier identifier, Controller controller) {
        return controllerRegistry.register(identifier, controller);
    }
}
