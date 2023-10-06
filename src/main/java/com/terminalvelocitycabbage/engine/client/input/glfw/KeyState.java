package com.terminalvelocitycabbage.engine.client.input.glfw;

import static org.lwjgl.glfw.GLFW.*;

public enum KeyState {

    RELEASE(GLFW_RELEASE),
    PRESS(GLFW_PRESS),
    REPEAT(GLFW_REPEAT);

    final int glfwCode;

    KeyState(int glfwCode) {
        this.glfwCode = glfwCode;
    }
}
