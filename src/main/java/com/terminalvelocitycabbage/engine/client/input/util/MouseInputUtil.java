package com.terminalvelocitycabbage.engine.client.input.util;

import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetMouseButton;
import static org.lwjgl.system.MemoryStack.stackPush;

public class MouseInputUtil {

    public static Vector2f getMousePosition(long window) {
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

    public static boolean isMouseButtonPressed(long window, int button) {
        return glfwGetMouseButton(window, button) == GLFW_PRESS;
    }

    public static boolean isLeftMouseButtonPressed(long window) {
        return isMouseButtonPressed(window, GLFW_MOUSE_BUTTON_LEFT);
    }

    public static boolean isRightMouseButtonPressed(long window) {
        return isMouseButtonPressed(window, GLFW_MOUSE_BUTTON_RIGHT);
    }

    public static boolean isMiddleMouseButtonPressed(long window) {
        return isMouseButtonPressed(window, GLFW_MOUSE_BUTTON_MIDDLE);
    }

    public static boolean isInWindow(long window) {
        return glfwGetWindowAttrib(window, GLFW_HOVERED) == GLFW_TRUE;
    }
}
