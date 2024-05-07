package com.terminalvelocitycabbage.engine.client.input.util;

import com.terminalvelocitycabbage.engine.client.input.types.ButtonAction;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWGamepadState;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Some utilities for getting useful information out of an {@link GLFWGamepadState} for use in TVE input handlers
 */
public class GamepadInputUtil {

    public static ButtonAction getButtonAStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_A));
    }

    public static ButtonAction getButtonBStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_B));
    }

    public static ButtonAction getButtonXStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_X));
    }

    public static ButtonAction getButtonYStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_Y));
    }

    public static ButtonAction getLeftBumperStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER));
    }

    public static ButtonAction getRightBumperStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER));
    }

    public static ButtonAction getBackButtonStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_BACK));
    }

    public static ButtonAction getStartButtonStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_START));
    }

    public static ButtonAction getGuideButtonStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_GUIDE));
    }

    public static ButtonAction getLeftJoystickPressedStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_LEFT_THUMB));
    }

    public static ButtonAction getRightJoystickPressedStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB));
    }

    public static ButtonAction getDPadUpStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP));
    }

    public static ButtonAction getDPadDownStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_DOWN));
    }

    public static ButtonAction getDPadLeftStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_LEFT));
    }

    public static ButtonAction getDPadRightStatus(GLFWGamepadState glfwGamepadState) {
        return ButtonAction.fromGLFW(glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT));
    }

    public static boolean isButtonAPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_A) == GLFW_PRESS;
    }

    public static boolean isButtonBPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_B) == GLFW_PRESS;
    }

    public static boolean isButtonXPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_X) == GLFW_PRESS;
    }

    public static boolean isButtonYPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_Y) == GLFW_PRESS;
    }

    public static boolean isLeftBumperPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER) == GLFW_PRESS;
    }

    public static boolean isRightBumperPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER) == GLFW_PRESS;
    }

    public static boolean isBackButtonPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_BACK) == GLFW_PRESS;
    }

    public static boolean isStartButtonPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_START) == GLFW_PRESS;
    }

    public static boolean isGuideButtonPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_GUIDE) == GLFW_PRESS;
    }

    public static boolean isLeftJoystickPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_LEFT_THUMB) == GLFW_PRESS;
    }

    public static boolean isRightJoystickPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB) == GLFW_PRESS;
    }

    public static boolean isDPadUpPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_UP) == GLFW_PRESS;
    }

    public static boolean isDPadDownPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_DOWN) == GLFW_PRESS;
    }

    public static boolean isDPadLeftPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_LEFT) == GLFW_PRESS;
    }

    public static boolean isDPadRightPressed(GLFWGamepadState glfwGamepadState) {
        return glfwGamepadState.buttons(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT) == GLFW_PRESS;
    }

    public static float getLeftJoystickX(GLFWGamepadState state) {
        //Normalized from -1 to 1
        return state.axes(GLFW_GAMEPAD_AXIS_LEFT_X);
    }

    public static float getLeftJoystickY(GLFWGamepadState state) {
        //Normalized from -1 to 1
        return state.axes(GLFW_GAMEPAD_AXIS_LEFT_Y);
    }

    public static float getRightJoystickX(GLFWGamepadState state) {
        //Normalized from -1 to 1
        return state.axes(GLFW_GAMEPAD_AXIS_RIGHT_X);
    }

    public static float getRightJoystickY(GLFWGamepadState state) {
        //Normalized from -1 to 1
        return state.axes(GLFW_GAMEPAD_AXIS_RIGHT_Y);
    }

    public static Vector2f getLeftJoystickXY(GLFWGamepadState state) {
        return new Vector2f(getLeftJoystickX(state), getLeftJoystickY(state));
    }

    public static Vector2f getRightJoystickXY(GLFWGamepadState state) {
        return new Vector2f(getRightJoystickX(state), getRightJoystickY(state));
    }

    public static float getLeftTriggerPressedAmount(GLFWGamepadState state) {
        //Normalized from 0 to 1
        return (state.axes(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER) + 1) / 2f;
    }

    public static float getRightTriggerPressedAmount(GLFWGamepadState state) {
        //Normalized from 0 to 1
        return (state.axes(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER) + 1) / 2f;
    }
}
