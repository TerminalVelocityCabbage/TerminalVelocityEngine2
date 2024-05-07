package com.terminalvelocitycabbage.engine.client.input.types;

import com.terminalvelocitycabbage.engine.debug.Log;

import static org.lwjgl.glfw.GLFW.*;

public abstract class GamepadInput {

    public enum Button {

        A(GLFW_GAMEPAD_BUTTON_A), //Same as CROSS
        B(GLFW_GAMEPAD_BUTTON_B), //Same as CIRCLE
        X(GLFW_GAMEPAD_BUTTON_X), //Same as SQUARE
        Y(GLFW_GAMEPAD_BUTTON_Y), //Same as TRIANGLE
        LEFT_BUMPER(GLFW_GAMEPAD_BUTTON_LEFT_BUMPER),
        RIGHT_BUMPER(GLFW_GAMEPAD_BUTTON_RIGHT_BUMPER),
        BACK(GLFW_GAMEPAD_BUTTON_BACK),
        START(GLFW_GAMEPAD_BUTTON_START),
        GUIDE(GLFW_GAMEPAD_BUTTON_GUIDE),
        LEFT_JOYSTICK_DOWN(GLFW_GAMEPAD_BUTTON_LEFT_THUMB),
        RIGHT_JOYSTICK_DOWN(GLFW_GAMEPAD_BUTTON_RIGHT_THUMB),
        DPAD_UP(GLFW_GAMEPAD_BUTTON_DPAD_UP),
        DPAD_DOWN(GLFW_GAMEPAD_BUTTON_DPAD_DOWN),
        DPAD_LEFT(GLFW_GAMEPAD_BUTTON_DPAD_LEFT),
        DPAD_RIGHT(GLFW_GAMEPAD_BUTTON_DPAD_RIGHT);

        final int glfwKey;

        Button(int glfwKey) {
            this.glfwKey = glfwKey;
        }

        public int getGlfwKey() {
            return glfwKey;
        }
    }

    public enum Joystick {

        L_UP(GLFW_GAMEPAD_AXIS_LEFT_Y, 1),
        L_DOWN(GLFW_GAMEPAD_AXIS_LEFT_Y, -1),
        L_LEFT(GLFW_GAMEPAD_AXIS_LEFT_X, -1),
        L_RIGHT(GLFW_GAMEPAD_AXIS_LEFT_X, 1),
        R_UP(GLFW_GAMEPAD_AXIS_RIGHT_Y, 1),
        R_DOWN(GLFW_GAMEPAD_AXIS_RIGHT_Y, -1),
        R_LEFT(GLFW_GAMEPAD_AXIS_RIGHT_X, -1),
        R_RIGHT(GLFW_GAMEPAD_AXIS_RIGHT_X, 1),
        LEFT_TRIGGER(GLFW_GAMEPAD_AXIS_LEFT_TRIGGER, 0),
        RIGHT_TRIGGER(GLFW_GAMEPAD_AXIS_RIGHT_TRIGGER, 0);

        final int glfwKey;
        final int direction; //-1 for down or left 1 for up or right, 0 for scalar normalization

        Joystick(int glfwKey, int direction) {
            this.glfwKey = glfwKey;
            this.direction = direction;
        }

        public float getNormalizedDirection(float input) {
            //We want to normalize the normally -1 to 1 input of a trigger to 0 to 1
            if (direction == 0) return (input + 1) / 2f;
            //We want only the negative value (as a positive float) of this axis and 0 if it's positive
            if (direction == -1) return Math.abs(Math.min(input, 0));
            //We want only the positive portion of this axis and 0 if it's negative
            if (direction == 1) return Math.max(input, 0);
            Log.warn("invalid input detected: " + input);
            return 0;
        }

        public int getGlfwKey() {
            return glfwKey;
        }
    }

}
