package com.terminalvelocitycabbage.engine.client.input;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.input.control.*;
import com.terminalvelocitycabbage.engine.client.input.controller.Controller;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryUtil;

import java.util.ArrayList;
import java.util.List;

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
    List<Identifier> activeControllers;
    Scene lastProcessedScene;

    public InputHandler() {
        this.gamepadState = new GLFWGamepadState(MemoryUtil.memAlloc(GLFWGamepadState.SIZEOF));
        this.controlRegistry = new Registry<>();
        this.controllerRegistry = new Registry<>();
        this.activeControllers = new ArrayList<>();
    }

    /**
     * Marks an input read state, this updates all queues for input
     *
     * @param focusedWindow1    The window which is selected
     * @param mousedOverWindow1 The window which the mouse is over (or -1 if none)
     * @param deltaTime The amount of time in ms between now and the last update
     */
    public void update(long focusedWindow1, long mousedOverWindow1, long deltaTime) {

        var properties = ClientBase.getInstance().getWindowManager().getPropertiesFromWindow(focusedWindow1);
        Scene currentScene = properties == null ? null : properties.getActiveScene();

        if (this.focusedWindow != focusedWindow1 || currentScene != lastProcessedScene) {
            setControllersForScene(currentScene);
            lastProcessedScene = currentScene;
        }

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
        activeControllers.forEach(identifier -> {
            var controller = controllerRegistry.get(identifier);
            if (controller != null) {
                controller.processInputs();
            }
        });
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
            case KeyboardKeyControl kkc -> controlRegistry.getAndRegister(ClientBase.getInstance().identifierOf("keyboardKey_control", kkc.getKey().name()), kkc).getElement();
            case GamepadButtonControl gpbc -> controlRegistry.getAndRegister(ClientBase.getInstance().identifierOf("gamepadButton_control", gpbc.getButton().name()), gpbc).getElement();
            case MouseButtonControl mbc -> controlRegistry.getAndRegister(ClientBase.getInstance().identifierOf("mouseButton_control", mbc.getButton().name()), mbc).getElement();
            case GamepadAxisControl gpac -> controlRegistry.getAndRegister(ClientBase.getInstance().identifierOf("gamepadAxis_control", gpac.getAxis().name()), gpac).getElement();
            case MouseMovementControl mmc -> controlRegistry.getAndRegister(ClientBase.getInstance().identifierOf("mouseMovement_control", mmc.getAxis().name()), mmc).getElement();
            case MouseScrollControl msc -> controlRegistry.getAndRegister(ClientBase.getInstance().identifierOf("mouseScroll_control", msc.getDirection().name()), msc).getElement();
        };
    }

    /**
     * Registers a group of controls which result in a common action. This is how you make the W key and forward on the
     * gamepad joystick both mean "forward" etc.
     * @param namespace An identifier which this controller can be identified by
     * @param name The name of this controller
     * @param controller A {@link Controller} which groups the desired controls
     * @return The Controller which was registered
     */
    public Identifier registerController(String namespace, String name, Controller controller) {
        return controllerRegistry.getAndRegister(new Identifier(namespace, "controller", name), controller).getIdentifier();
    }

    /**
     * @return The list of active controller identifiers
     */
    public List<Identifier> getActiveControllers() {
        return activeControllers;
    }

    /**
     * Activates the specified controller
     * @param identifier The identifier of the controller to activate
     */
    public void activateController(Identifier identifier) {
        if (!activeControllers.contains(identifier)) {
            activeControllers.add(identifier);
        }
    }

    /**
     * Deactivates the specified controller
     * @param identifier The identifier of the controller to deactivate
     */
    public void deactivateController(Identifier identifier) {
        activeControllers.remove(identifier);
    }

    /**
     * Clears all active controllers
     */
    public void clearActiveControllers() {
        activeControllers.clear();
    }

    /**
     * Sets the active controllers to those specified by the scene
     * @param scene The scene to get controllers from
     */
    public void setControllersForScene(Scene scene) {
        clearActiveControllers();
        if (scene != null) {
            scene.getInputControllers().forEach(this::activateController);
        }
    }
}
