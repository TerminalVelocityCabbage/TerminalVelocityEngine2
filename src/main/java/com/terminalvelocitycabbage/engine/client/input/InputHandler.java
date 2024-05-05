package com.terminalvelocitycabbage.engine.client.input;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.input.types.KeyInput;
import com.terminalvelocitycabbage.engine.debug.Log;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;

//It may be better to use glfwGetKey and glfwGetCursorPos and friends as opposed to the key callbacks because the key
//callbacks are not timed with the rest of the engine. we can update the input handler exactly when we want to if we
//get the status of a list of listened to keys.

//The hard part of doing the above is for character input for chat windows and things like that. we want to listen to
//Everything. So I think that it might make the most sense to use a combination of both? Key callback seems usless
//for most things, except it might be useful when we want to listen for new keybinds in an ingame configuration. Like
//"press which key you want to be for walk" etc. We will need the key callback for that.

public class InputHandler {

    public void update() {
//        Log.info("Key W: " + KeyInput.Action.fromGLFW(glfwGetKey(ClientBase.getInstance().getWindowManager().getFocusedWindow(), GLFW_KEY_W)));

//        try (MemoryStack stack = stackPush()) {
//            DoubleBuffer cx = stack.mallocDouble(1);
//            DoubleBuffer cy = stack.mallocDouble(1);
//            long mousedOverWindow = ClientBase.getInstance().getWindowManager().getMousedOverWindow();
//            if (mousedOverWindow != -1) {
//                glfwGetCursorPos(mousedOverWindow, cx, cy);
//                Log.info("On-Demand Cursor Pos: " + cx.get(0) + " : " + cy.get(0));
//            }
//        }

//        var window = ClientBase.getInstance().getWindowManager().getMousedOverWindow();
//        if (window != -1) {
//            Log.info("Mouse Button 1: " + KeyInput.Action.fromGLFW(glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_1)));
//        }
    }

    //Handle current input device status (auto switch between input devices)
    //Dispatch events for input connection/disconnection
    //Dispatch events for when active controller changes (keyboard/controller)

}
