package com.terminalvelocitycabbage.engine.client.input;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.input.types.ButtonAction;
import com.terminalvelocitycabbage.engine.debug.Log;

/**
 * A class that converts raw glfw input callback events and converts them into listenable actions
 */
public class InputMapper {

    /**
     * @param window The window which published this key callback
     * @param key The glfw recognized key that was pressed (not all are recognized)
     * @param scancode The unique system specific code which represents this key being pressed
     * @param action The action that represents this key action Easily mapped to {@link ButtonAction}
     * @param mods
     */
    public void keyCallback(long window, int key, int scancode, int action, int mods) {
//        Log.info("Key Input: " +
//                ClientBase.getInstance().getWindowManager().getPropertiesFromWindow(window).getTitle() +
//                " " + GLFW.glfwGetKeyName(key, scancode) +
//                " " + scancode +
//                " " + KeyInput.Action.fromGLFW(action).getTypeName() +
//                " " + mods);
    }


    public void charCallback(long window, int character) {
//        Log.info("Character Input: " +
//                ClientBase.getInstance().getWindowManager().getPropertiesFromWindow(window).getTitle() +
//                " " + String.copyValueOf(Character.toChars(character)));
    }

    public void cursorPosCallback(long window, double x, double y) {
//        Log.info("Cursor Pos: " +
//                ClientBase.getInstance().getWindowManager().getPropertiesFromWindow(window).getTitle() +
//                " " + x + " : " + y
//        );
    }

    public void mouseButtonCallback(long window, int button, int action, int mods) {
//        Log.info("Mouse Button Input: " +
//                ClientBase.getInstance().getWindowManager().getPropertiesFromWindow(window).getTitle() +
//                " " + button +
//                " " + KeyInput.Action.fromGLFW(action).getTypeName() +
//                " " + mods);
    }

    //TODO note that this is called more than once per frame often times, so track more than one update
    public void scrollCallback(long window, double deltaX, double deltaY) {
        Log.info("Scrolling: " +
                ClientBase.getInstance().getWindowManager().getPropertiesFromWindow(window).getTitle() +
                " " + deltaX +
                ", " + deltaY);
    }
}
