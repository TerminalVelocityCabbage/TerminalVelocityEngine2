package com.terminalvelocitycabbage.engine.client.input.types;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;

/**
 * A value that represents a button type, down, up, held, or invalid
 */
public enum ButtonAction {

    PRESSED(GLFW_PRESS, "PRESSED"),
    RELEASED(GLFW_RELEASE, "RELEASED"),
    REPEAT(GLFW_REPEAT, "REPEAT"), //Note to user this is not a reliable way to know if a key is being held down
    INVALID(-1, "INVALID");

    final int glfwAction;
    final String typeName;

    ButtonAction(int glfwAction, String typeName) {
        this.glfwAction = glfwAction;
        this.typeName = typeName;
    }

    /**
     * @param action The GLFW mapping for the requested action
     * @return The resulting Button Action
     */
    public static ButtonAction fromGLFW(int action) {
        return switch (action) {
            case GLFW_PRESS -> PRESSED;
            case GLFW_RELEASE -> RELEASED;
            case GLFW_REPEAT -> REPEAT;
            default -> INVALID;
        };
    }

    /**
     * @return The human-readable name that represents this action
     */
    public String getTypeName() {
        return typeName;
    }
}
