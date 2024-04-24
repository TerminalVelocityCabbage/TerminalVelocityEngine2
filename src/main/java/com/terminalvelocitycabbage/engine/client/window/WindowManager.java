package com.terminalvelocitycabbage.engine.client.window;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

public class WindowManager {

    GLFWErrorCallback errorCallback;
    GLFWKeyCallback keyCallback;
    GLFWFramebufferSizeCallback framebufferSizeCallback;
    Callback debugProc;
    GLFWVidMode videoMode;

    //Stores a list of window handles that need to be destroyed on the main thread
    private List<WindowThread> windowsToDestroy = Collections.synchronizedList(new ArrayList<>());
    //Some monitor info (this should be expanded in the future)
    private int scaleX;
    //The (usually) active windows of this manager
    private Map<Long, WindowThread> threads = new HashMap<>();

    //Initialize this window manager (glfw)
    public void init() {

        //Create the error callback
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        //Throw an error if glfw fails to init
        if (!glfwInit()) Log.crash("Unable to initialize GLFW", new IllegalStateException("GLFW was not initialized"));

        //configure glfw
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_SCALE_TO_MONITOR, GLFW_TRUE);
        if (Platform.get() == Platform.MACOSX) {
            glfwWindowHint(GLFW_COCOA_RETINA_FRAMEBUFFER, GLFW_FALSE);
        }

        //get monitor content scale (this should be expanded)
        try (MemoryStack s = stackPush()) {
            FloatBuffer px = s.mallocFloat(1);
            glfwGetMonitorContentScale(glfwGetPrimaryMonitor(), px, null);
            scaleX = (int)px.get(0);
        }
    }

    //The actual window update loop
    public void loop() {
        while (true) {

            //Destroy all destroyable windows before polling for events
            windowsToDestroy.forEach(window -> {
                System.out.println("Made it to the end of life of a window");
                destroyWindow(window);
                System.out.println("destroyed window " + window);
            });
            //Reset for the next loop
            windowsToDestroy.clear();

            //Poll for window events (like input or closing etc.)
            glfwWaitEvents();

            //Don't update the threads if there is nothing to update
            if (!hasAliveWindow()) break;

            //Check for window close requests
            threads.forEach((window, glfwThread) -> {
                if (glfwWindowShouldClose(window)) glfwThread.destroyThread();
            });
        }
    }

    //Destroys this window manager and glfw context
    public void destroy() {

        //Mark all windows as needing to quit
        threads.forEach((window, glfwThread) -> {
            glfwThread.quit = true;
        });

        //wait for all window threads to die by updating this list of boolean states until no more are true
        List<Boolean> threadStates;
        int numAlive;
        do {
            numAlive = 0;
            threadStates = threads.values().stream().map(Thread::isAlive).toList();
            for (int i = 0; i < threadStates.size(); i++) {
                if (threadStates.get(i)) numAlive++;
            }
        } while (numAlive != 0);

        //Terminate this glfw instance
        glfwTerminate();
        //Free the error callback
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    //Create a new window in this manager
    public void createNewWindow(WindowProperties properties) {

        //Create the glfw window
        long window = glfwCreateWindow(properties.getWidth(), properties.getHeight(), properties.getTitle(), NULL, NULL);
        //Error if the window is not created successfully
        if (window == NULL) {
            throw new IllegalStateException("Failed to create GLFW window.");
        }

        //Add this window to the list of active window threads
        threads.put(window, new WindowThread(window, this, properties));

        //Set key callback
        //TODO differ this to input handler
        glfwSetKeyCallback(window, keyCallback = new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                ClientBase.getInstance().keyCallback(window, key, scancode, action, mods);
            }
        });

        //Set framebuffer size callback
        glfwSetFramebufferSizeCallback(window, framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
            public void invoke(long window, int w, int h) {
                if (w > 0 && h > 0) {
                    properties.setWidth(w);
                    properties.setHeight(h);
                }
            }
        });

        //Center the window on the primary monitor
        videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(window, (videoMode.width() - properties.getWidth()) / 2, (videoMode.height() - properties.getHeight()) / 2);

        //Update the window size variables based on post-init dimensions before showing the window
        try (MemoryStack frame = MemoryStack.stackPush()) {
            IntBuffer framebufferSize = frame.mallocInt(2);
            nglfwGetFramebufferSize(window, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
            properties.setWidth(framebufferSize.get(0));
            properties.setHeight(framebufferSize.get(1));
        }

        //Set the window title
        glfwSetWindowTitle(window, properties.getTitle());

        //Show the window
        glfwShowWindow(window);

        //Start the window update loop
        threads.get(window).start();
    }

    //Destroys the specified window
    //This MUST always be called from the main thread
    private void destroyWindow(WindowThread thread) {
        thread.destroyWindow();
        //Prevent an IllegalStateException on last destroyed window
        if (threads.size() > 1) {
            threads.remove(thread);
        }
    }

    //Returns true if this window manager has any alive windows
    private boolean hasAliveWindow() {
        for (WindowThread thread : threads.values()) {
            if (!thread.quit) return true;
        }
        return false;
    }

    public void queueDestroyWindow(WindowThread thread) {
        windowsToDestroy.add(thread);
    }

    public WindowProperties getPropertiesFromWindow(long windowHandle) {
        return threads.get(windowHandle).getProperties();
    }
}
