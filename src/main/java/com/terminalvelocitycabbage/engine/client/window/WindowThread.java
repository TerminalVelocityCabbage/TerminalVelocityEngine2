package com.terminalvelocitycabbage.engine.client.window;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;

public class WindowThread extends Thread {

    //The pointer to this window
    final long windowHandle;
    //Whether this thread should be closed
    boolean quit;
    //The handling Window Manager of this window thread
    final WindowManager windowManager;
    //Properties of this window
    WindowProperties properties;

    /**
     * @param windowHandle the window pointer that points to the window managed by this thread
     * @param windowManager the manager that created this window thread
     */
    protected WindowThread(long windowHandle, WindowManager windowManager, WindowProperties properties) {
        this.windowHandle = windowHandle;
        this.quit = false;
        this.windowManager = windowManager;
        this.properties = properties;
    }

    @Override
    public void run() {
        //make this thread use this context for this new window
        glfwMakeContextCurrent(windowHandle);
        GL.createCapabilities();

        //Turn on vsync
        //TODO swap this out for a window config apply()
        glfwSwapInterval(1);

        //swap the image in this window with the new one
        while (!quit) {
            getProperties().getRenderer().update(getProperties());
            glfwSwapBuffers(windowHandle);
        }

        //queue this window for destruction
        windowManager.queueDestroyWindow(windowHandle);

        //Clear the gl capabilities from this window
        GL.setCapabilities(null);
    }

    public WindowProperties getProperties() {
        return properties;
    }
}
