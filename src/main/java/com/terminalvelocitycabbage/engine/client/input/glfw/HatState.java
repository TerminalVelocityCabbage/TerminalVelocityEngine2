package com.terminalvelocitycabbage.engine.client.input.glfw;

import static org.lwjgl.glfw.GLFW.*;

public enum HatState {

    CENTERED(GLFW_HAT_CENTERED),
    UP(GLFW_HAT_UP),
    RIGHT(GLFW_HAT_RIGHT),
    DOWN(GLFW_HAT_DOWN),
    LEFT(GLFW_HAT_LEFT),
    RIGHT_UP(GLFW_HAT_RIGHT_UP),
    RIGHT_DOWN(GLFW_HAT_RIGHT_DOWN),
    LEFT_UP(GLFW_HAT_LEFT_UP),
    LEFT_DOWN(GLFW_HAT_LEFT_DOWN);

    final int glfwCode;

    HatState(int glfwCode) {
        this.glfwCode = glfwCode;
    }
}
