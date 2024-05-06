package com.terminalvelocitycabbage.engine.client.input.util;

import static org.lwjgl.glfw.GLFW.*;

public class KeyboardInputUtil {

    public static ButtonAction getKeyStatus(long window, int glfwKey) {
        return ButtonAction.fromGLFW(glfwGetKey(window, glfwKey));
    }

    public static boolean isKeyPressed(long window, int glfwKey) {
        return glfwGetKey(window, glfwKey) == GLFW_PRESS;
    }

}
