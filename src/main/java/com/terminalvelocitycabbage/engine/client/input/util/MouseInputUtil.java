package com.terminalvelocitycabbage.engine.client.input.util;

import com.terminalvelocitycabbage.engine.client.input.types.ButtonAction;
import com.terminalvelocitycabbage.engine.client.input.types.MouseButtonInput;
import com.terminalvelocitycabbage.engine.debug.Log;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFWGamepadState;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.system.MemoryStack.stackPush;

public class MouseInputUtil {

    public static Vector2f getMousePosition(long window) {
        if (window == -1) {
            Log.warn("Tried to get mouse pos with no focused window");
            return null;
        }
        try (MemoryStack stack = stackPush()) {
            DoubleBuffer cx = stack.mallocDouble(1);
            DoubleBuffer cy = stack.mallocDouble(1);
            glfwGetCursorPos(window, cx, cy);
            return new Vector2f((float)cx.get(), (float)cy.get());
        }
    }

    public static ButtonAction getMouseButtonStatus(long window, int button) {
        return ButtonAction.fromGLFW(glfwGetMouseButton(window, button));
    }

    public static boolean isMouseButtonPressed(long window, MouseButtonInput button) {
        if (window == -1) return false;
        return glfwGetMouseButton(window, button.getGlfwKey()) == GLFW_PRESS;
    }

    public static boolean isLeftMouseButtonPressed(long window) {
        return isMouseButtonPressed(window, MouseButtonInput.LEFT_CLICK);
    }

    public static boolean isRightMouseButtonPressed(long window) {
        return isMouseButtonPressed(window, MouseButtonInput.RIGHT_CLICK);
    }

    public static boolean isMiddleMouseButtonPressed(long window) {
        return isMouseButtonPressed(window, MouseButtonInput.MIDDLE_CLICK);
    }

    public static boolean isInWindow(long window) {
        if (window == -1) return false;
        return glfwGetWindowAttrib(window, GLFW_HOVERED) == GLFW_TRUE;
    }
}
