package com.terminalvelocitycabbage.engine.client.window;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.RendererBase;
import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.util.ClassUtils;
import com.terminalvelocitycabbage.engine.util.MutableInstant;
import com.terminalvelocitycabbage.engine.util.touples.Pair;
import org.lwjgl.opengl.GL;

import javax.management.ReflectionException;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
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
    //A clock to manage deltaTime for this window's renderer
    final MutableInstant rendererClock;

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
        GL.createCapabilities();

        //Turn on vsync
        //TODO swap this out for a window config apply() && Verify that bgfx may take care of this instead
        glfwSwapInterval(1);

        //Create an instance of this renderer and init it
        RendererBase renderer;
        try {
            Pair<Class<? extends RendererBase>, RenderGraph> registryPair = ClientBase.getInstance().getRendererRegistry().get(properties.getRenderer());
            renderer = ClassUtils.createInstance(registryPair.getValue0(), registryPair.getValue1());
        } catch (ReflectionException e) {
            throw new RuntimeException(e);
        }

        //Initialize this renderer
        renderer.init(getProperties(), windowHandle);

        //swap the image in this window with the new one
        long deltaTime;
        while (!quit) {
            deltaTime = rendererClock.getDeltaTime();
            rendererClock.now();
            renderer.render(getProperties(), deltaTime);
            glfwSwapBuffers(windowHandle);
        }

        //Destroy the renderer
        renderer.destroy();

        //queue this window for destruction
        windowManager.queueDestroyWindow(this);

        //Clear the gl capabilities from this window
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
}
