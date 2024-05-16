package com.terminalvelocitycabbage.engine.client.input.util;

import com.terminalvelocitycabbage.engine.client.input.types.ButtonAction;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

public class KeyboardInputUtil {

    public static ButtonAction getKeyStatus(long window, int glfwKey) {
        if (window == -1) return null;
        return ButtonAction.fromGLFW(glfwGetKey(window, glfwKey));
    }

    public static boolean isKeyPressed(long window, int glfwKey) {
        if (window == -1) return false;
        return glfwGetKey(window, glfwKey) == GLFW_PRESS;
    }

}
