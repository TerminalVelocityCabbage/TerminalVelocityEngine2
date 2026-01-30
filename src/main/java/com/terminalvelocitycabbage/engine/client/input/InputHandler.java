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

/**
 * This is the class which handles all Input. Here you register controls, controllers, and controller sets for handling
 * and managing the way your game listens to raw input and translates that into actions in the game.
 */
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
     * @param deltaTime The amount of time in ms between now and the last update
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
                case MouseScrollControl mouseScrollControl -> mouseScrollControl.update(this, deltaTime);
            }
        }
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
                case MouseScrollControl mouseScrollControl -> { }
            }
        }
    }

    /**
     * @return The current GLFWGamepadState
     */
    public GLFWGamepadState getGamepadState() {
        return gamepadState;
    }

    /**
     * @return A long which is the pointer to the currently active glfw window
     */
    public long getFocusedWindow() {
        return focusedWindow;
    }

    /**
     * @return A long which is the pointer to the currently moused over glfw window
     */
    public long getMousedOverWindow() {
        return mousedOverWindow;
    }

    /**
     * This is how the InputHandler knows to listen to input from a raw input device piece, like a key, button, joystick, etc.
     * @param control The {@link Control} which defines the Raw input that shall be listened to
     * @return The instance of the Control which was registered.
     */
    public Control registerControlListener(Control control) {
        return switch (control) {
            case KeyboardKeyControl kkc -> controlRegistry.getAndRegister(ClientBase.getInstance().identifierOf("keyboardKey_control_" + kkc.getKey().name()), kkc).getElement();
            case GamepadButtonControl gpbc -> controlRegistry.getAndRegister(ClientBase.getInstance().identifierOf("gamepadButton_control_" + gpbc.getButton().name()), gpbc).getElement();
            case MouseButtonControl mbc -> controlRegistry.getAndRegister(ClientBase.getInstance().identifierOf("mouseButton_control_" + mbc.getButton().name()), mbc).getElement();
            case GamepadAxisControl gpac -> controlRegistry.getAndRegister(ClientBase.getInstance().identifierOf("gamepadAxis_control_" + gpac.getAxis().name()), gpac).getElement();
            case MouseMovementControl mmc -> controlRegistry.getAndRegister(ClientBase.getInstance().identifierOf("mouseMovement_control_" + mmc.getAxis().name()), mmc).getElement();
            case MouseScrollControl msc -> controlRegistry.getAndRegister(ClientBase.getInstance().identifierOf("mouseScroll_control_" + msc.getDirection().name()), msc).getElement();
        };
    }

    /**
     * Registers a group of controls which result in a common action. This is how you make the W key and forward on the
     * gamepad joystick both mean "forward" etc.
     * @param identifier An identifier which this controller can be identified by
     * @param controller A {@link Controller} which groups the desired controls
     * @return The Controller which was registered
     */
    public Controller registerController(Identifier identifier, Controller controller) {
        return controllerRegistry.getAndRegister(identifier, controller).getElement();
    }
}
