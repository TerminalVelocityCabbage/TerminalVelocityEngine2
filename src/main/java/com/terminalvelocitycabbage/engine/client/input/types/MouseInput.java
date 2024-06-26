package com.terminalvelocitycabbage.engine.client.input.types;

import static org.lwjgl.glfw.GLFW.*;

/**
 * Any input type relating to the Muse
 */
public class MouseInput {

    /**
     * Buttons on a mouse
     */
    public enum Button {
        BUTTON_1(GLFW_MOUSE_BUTTON_1),
        BUTTON_2(GLFW_MOUSE_BUTTON_2),
        BUTTON_3(GLFW_MOUSE_BUTTON_3),
        BUTTON_4(GLFW_MOUSE_BUTTON_4),
        BUTTON_5(GLFW_MOUSE_BUTTON_5),
        BUTTON_6(GLFW_MOUSE_BUTTON_6),
        BUTTON_7(GLFW_MOUSE_BUTTON_7),
        BUTTON_8(GLFW_MOUSE_BUTTON_8),
        RIGHT_CLICK(GLFW_MOUSE_BUTTON_RIGHT),
        LEFT_CLICK(GLFW_MOUSE_BUTTON_LEFT),
        MIDDLE_CLICK(GLFW_MOUSE_BUTTON_MIDDLE);

        final int glfwKey;

        Button(int glfwKey) {
            this.glfwKey = glfwKey;
        }

        /**
         * @return The raw glfw input mapping for this button.
         */
        public int getGlfwKey() {
            return glfwKey;
        }
    }

    /**
     * A way to break down the directions a mouse moves into individual 0 to 1 mappable values
     */
    public enum MovementAxis {
        UP,
        DOWN,
        LEFT,
        RIGHT;
    }

    /**
     * A way to break down the directions a mouse can scroll into individual 0 to 1 values
     */
    public enum ScrollDirection {
        UP,
        DOWN,
        LEFT,
        RIGHT;
    }
}
