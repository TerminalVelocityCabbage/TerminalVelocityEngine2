package com.terminalvelocitycabbage.engine.client.input.util;

import com.terminalvelocitycabbage.engine.client.input.types.ButtonAction;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.glfwGetKey;

/**
 * A set of utilities for getting useful information about keyboard keys used internally in TVE or if a user needs to
 * query something manually using TVE Enums
 */
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
