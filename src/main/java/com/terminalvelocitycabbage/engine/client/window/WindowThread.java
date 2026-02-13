package com.terminalvelocitycabbage.engine.client.window;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.util.MutableInstant;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.nanovg.NanoVGGL3.*;
import static org.lwjgl.opengl.GL11.*;

public class WindowThread extends Thread {

    //The pointer to this window
    final long windowHandle;
    //Whether this thread should be closed
    boolean quit;
    //The handling Window Manager of this window thread
    final WindowManager windowManager;
    //Properties of this window
    WindowProperties properties;
    //A clock to manage deltaTime for this window's renderer
    final MutableInstant rendererClock;
    //Time
    long deltaTime;
    long runtime;

    /**
     * @param windowHandle the window pointer that points to the window managed by this thread
     * @param windowManager the manager that created this window thread
     */
    protected WindowThread(long windowHandle, WindowManager windowManager, WindowProperties properties) {
        this.windowHandle = windowHandle;
        this.quit = false;
        this.windowManager = windowManager;
        this.properties = properties;
        this.rendererClock = MutableInstant.ofNow();
    }

    @Override
    public void run() {
        //make this thread use this context for this new window
        glfwMakeContextCurrent(windowHandle);
        GLCapabilities capabilities = GL.createCapabilities();

        long nvg = nvgCreate(NVG_ANTIALIAS | NVG_STENCIL_STROKES);
        ClientBase.getInstance().setNvgContext(nvg);

        glEnable(GL_DEPTH_TEST);

        //Turn on vsync
        //TODO swap this out for a window config apply() && Verify that bgfx may take care of this instead
        glfwSwapInterval(1);

        //Initialize the RenderGraph & scene
        properties.init();
        ClientBase.getInstance().getRenderGraphRegistry().get(properties.getActiveScene().getRenderGraph()).init(capabilities);

        //swap the image in this window with the new one
        while (!quit) {
            deltaTime = rendererClock.getDeltaTime();
            runtime += deltaTime;
            rendererClock.now();

            //Make sure window properties are correct with current window size
            int[] width = new int[1];
            int[] height = new int[1];
            glfwGetFramebufferSize(windowHandle, width, height);
            properties.resize(width[0], height[0]);
            if (properties.isResized()) glViewport(0, 0, properties.getWidth(), properties.getHeight());

            //Clear the last frame and render a new one
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            ClientBase.getInstance().getRenderGraphRegistry().get(properties.getActiveScene().getRenderGraph()).render(getProperties(), deltaTime);
            properties.endFrame();
            glfwSwapBuffers(windowHandle);
        }

        //queue this window for destruction
        windowManager.queueDestroyWindow(this);

        //Clear the gl capabilities from this window
        nvgDelete(ClientBase.getInstance().getNvgContext());
        GL.setCapabilities(null);
    }

    /**
     * Destroys the current glfw window and frees it's callbacks
     */
    public void destroyWindow() {
        glfwFreeCallbacks(windowHandle);
        glfwDestroyWindow(windowHandle);
    }

    /**
     * Queues this thread for destruction when it can safely do so
     */
    public void destroyThread() {
        quit = true;
    }

    /**
     * @return The {@link WindowProperties} of this window
     */
    public WindowProperties getProperties() {
        return properties;
    }

    /**
     * @return The GLFW window handle for this window
     */
    public long getWindowHandle() {
        return windowHandle;
    }

    /**
     * @return this renderer's deltaTime
     */
    public long getDeltaTime() {
        return deltaTime;
    }

    public long getRuntime() {
        return runtime;
    }
}
