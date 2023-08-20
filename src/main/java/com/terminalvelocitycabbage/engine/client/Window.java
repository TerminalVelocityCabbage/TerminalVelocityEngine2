package com.terminalvelocitycabbage.engine.client;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

public class Window {

    GLFWErrorCallback errorCallback;
    GLFWKeyCallback keyCallback;
    GLFWFramebufferSizeCallback framebufferSizeCallback;
    Callback debugProc;

    long windowID;
    int width = 600;
    int height = 400;
    final Object lock = new Object();
    boolean destroyed;

    Identifier activeRenderer;

    public void run() {
        try {
            init();
            winProcLoop();
            destroy();
        } finally {
            glfwTerminate();
            Objects.requireNonNull(glfwSetErrorCallback(null)).free();
        }
    }

    private void init() {

        //Create the error callback
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));

        //check that we can init glfw
        if (!glfwInit()) Log.crash("Unable to initialize GLFW", new IllegalStateException("GLFW was not initialized"));

        //Configure the window to not be shown yet
        //TODO make a window config like last version to configure these things
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        //Create the window
        windowID = glfwCreateWindow(width, height, "Window Title", NULL, NULL);
        if (windowID == NULL) Log.crash("Failed to create GLFW Window", new IllegalStateException("windowID is NULL"));

        //Set key callback
        //TODO differ this to input handler
        glfwSetKeyCallback(windowID, keyCallback = new GLFWKeyCallback() {
            public void invoke(long window, int key, int scancode, int action, int mods) {
                ClientBase.getInstance().keyCallback(window, key, scancode, action, mods);
            }
        });

        //Set framebuffer size callback
        glfwSetFramebufferSizeCallback(windowID, framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
            public void invoke(long window, int w, int h) {
                if (w > 0 && h > 0) {
                    width = w;
                    height = h;
                }
            }
        });

        //Center the window on the primary monitor
        GLFWVidMode videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(windowID, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2);

        //Update the window size variables based on post-init dimensions before showing the window
        try (MemoryStack frame = MemoryStack.stackPush()) {
            IntBuffer framebufferSize = frame.mallocInt(2);
            nglfwGetFramebufferSize(windowID, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
            width = framebufferSize.get(0);
            height = framebufferSize.get(1);
        }

        //Finally show the window
        glfwShowWindow(windowID);
    }

    void renderLoop() {

        //Attach gl context to this window
        glfwMakeContextCurrent(windowID);
        GL.createCapabilities();

        //Setup error callbacks
        debugProc = GLUtil.setupDebugMessageCallback();

        //Give the window a default clear color, this can be changed later
        glClearColor(0.3f, 0.5f, 0.7f, 0.0f);

        //Run the loop
        while (!destroyed) {

            //Update the client
            ClientBase.getInstance().update();

            //Push a new frame if the window was not destroyed
            synchronized (lock) {
                if (!destroyed) {
                    glfwSwapBuffers(windowID);
                }
            }
        }
    }

    void winProcLoop() {

        //Start new thread to have the OpenGL context current in and which does the rendering.
        new Thread(this::renderLoop).start();

        //Listen for changes to the registered callbacks
        while (!glfwWindowShouldClose(windowID)) {
            glfwWaitEvents();
        }
    }

    private void destroy() {

        //Close the window
        synchronized (lock) {
            destroyed = true;
            glfwDestroyWindow(windowID);
        }

        //Free the debug proc
        if (debugProc != null) {
            debugProc.free();
        }

        //Free the callbacks
        keyCallback.free();
        framebufferSizeCallback.free();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Identifier getActiveRenderer() {
        return activeRenderer;
    }

    public void setActiveRenderer(Identifier activeRenderer) {
        this.activeRenderer = activeRenderer;
    }
}
