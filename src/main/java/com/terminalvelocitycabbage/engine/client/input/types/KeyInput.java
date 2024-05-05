package com.terminalvelocitycabbage.engine.client.input.types;

import static org.lwjgl.glfw.GLFW.*;

public class KeyInput {

    public enum Action {

        PRESSED(GLFW_PRESS, "PRESSED"),
        RELEASED(GLFW_RELEASE, "RELEASED"),
        REPEAT(GLFW_REPEAT, "REPEAT"), //Note to user this is not a reliable way to know if a key is being held down
        INVALID(-1, "INVALID");

        final int glfwAction;
        final String typeName;

        Action(int glfwAction, String typeName) {
            this.glfwAction = glfwAction;
            this.typeName = typeName;
        }

        public static Action fromGLFW(int action) {
            return switch (action) {
                case GLFW_PRESS -> PRESSED;
                case GLFW_RELEASE -> RELEASED;
                case GLFW_REPEAT -> REPEAT;
                default -> INVALID;
            };
        }

        public String getTypeName() {
            return typeName;
        }
    }

}
