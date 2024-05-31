package com.terminalvelocitycabbage.engine.client.input.util;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.window.InputCallbackListener;
import com.terminalvelocitycabbage.engine.client.input.types.ButtonAction;
import com.terminalvelocitycabbage.engine.client.input.types.MouseInput;
import com.terminalvelocitycabbage.engine.debug.Log;
import org.joml.Vector2f;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

/**
 * A set uf mouse input utilities to get useful information for use in the engine or if a user needs to handle something
 * manually themselves using TVE enums.
 */
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

    public static boolean isMouseButtonPressed(long window, MouseInput.Button button) {
        if (window == -1) return false;
        return glfwGetMouseButton(window, button.getGlfwKey()) == GLFW_PRESS;
    }

    public static boolean isLeftMouseButtonPressed(long window) {
        return isMouseButtonPressed(window, MouseInput.Button.LEFT_CLICK);
    }

    public static boolean isRightMouseButtonPressed(long window) {
        return isMouseButtonPressed(window, MouseInput.Button.RIGHT_CLICK);
    }

    public static boolean isMiddleMouseButtonPressed(long window) {
        return isMouseButtonPressed(window, MouseInput.Button.MIDDLE_CLICK);
    }

    public static boolean isInWindow(long window) {
        if (window == -1) return false;
        return glfwGetWindowAttrib(window, GLFW_HOVERED) == GLFW_TRUE;
    }

    public static float getAxisAmount(MouseInput.MovementAxis axis) {

        InputCallbackListener inputCallbackListener = ClientBase.getInstance().getInputCallbackListener();

        return switch (axis) {
            case UP -> inputCallbackListener.getMouseUpwardDelta();
            case DOWN -> inputCallbackListener.getMouseDownwardDelta();
            case LEFT -> inputCallbackListener.getMouseLeftwardDelta();
            case RIGHT -> inputCallbackListener.getMouseRightwardDelta();
        };
    }

    public static float getScrollAmount(MouseInput.ScrollDirection direction) {

        InputCallbackListener inputCallbackListener = ClientBase.getInstance().getInputCallbackListener();

        return switch (direction) {
            case UP -> inputCallbackListener.getMouseScrollUpDelta();
            case DOWN -> inputCallbackListener.getMouseScrollDownDelta();
            case LEFT -> inputCallbackListener.getMouseScrollLeftDelta();
            case RIGHT -> inputCallbackListener.getMouseScrollRightDelta();
        };
    }
}
