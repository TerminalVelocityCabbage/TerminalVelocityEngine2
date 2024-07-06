package com.terminalvelocitycabbage.engine.client.window;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.Platform;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.Collectors;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

public class WindowManager {

    GLFWErrorCallback errorCallback;
    Callback debugProc;
    GLFWVidMode videoMode;

    //Stores a list of window handles that need to be destroyed on the main thread
    private List<WindowThread> windowsToDestroy = Collections.synchronizedList(new ArrayList<>());
    //Some monitor info (this should be expanded in the future)
    private int scaleX;
    //The (usually) active windows of this manager
    private Map<Long, WindowThread> threads = new HashMap<>();

    /**
     * Initializes this window manager and glfw
     */
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

    /**
     * The actual window update loop
     * @return Whether this game context should stop
     */
    public boolean loop() {
        //Destroy all destroyable windows before polling for events
        windowsToDestroy.forEach(window -> {
            System.out.println("Made it to the end of life of a window");
            destroyWindow(window);
            System.out.println("destroyed window " + window);
        });
        //Reset for the next loop
        windowsToDestroy.clear();

        //Poll for window events (like input or closing etc.)
        glfwPollEvents();
        //TODO make this configurable? poll events is good for always listening, wait events does not push a new frame unless there was an event
        //wait events does not work for controllers, it only listens for callback updates.
        //glfwWaitEvents();

        //Don't update the threads if there is nothing to update
        if (!hasAliveWindow()) return true;

        //Check for window close requests
        threads.forEach((window, glfwThread) -> {
            if (glfwWindowShouldClose(window)) glfwThread.destroyThread();
        });

        return false;
    }

    /**
     * Destroys this window manager and glfw context
     */
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

    /**
     * Creates a new window in this manager and opens it
     * @param properties The properties that the new window should posess
     * @return the glfw window handle of the newly created window
     */
    public long createNewWindow(WindowProperties properties) {

        //Create the glfw window
        long windowID = glfwCreateWindow(properties.getWidth(), properties.getHeight(), properties.getTitle(), NULL, NULL);
        //Error if the window is not created successfully
        if (windowID == NULL) {
            throw new IllegalStateException("Failed to create GLFW window.");
        }

        //Add this window to the list of active window threads
        threads.put(windowID, new WindowThread(windowID, this, properties));

        //Set framebuffer size callback
        glfwSetFramebufferSizeCallback(windowID, (long window, int w, int h) -> {
            properties.resize(w, h);
        });

        //Set focus state callback
        glfwSetWindowFocusCallback(windowID, (long window, boolean isActive) -> {
            properties.setFocused(isActive);
        });

        //Set mouse enter callback
        glfwSetCursorEnterCallback(windowID, (window, entered) -> {
            properties.setMousedOver(entered);
        });

        //Set char callback
        glfwSetCharCallback(windowID, (long window, int character) -> {
            ClientBase.getInstance().getInputCallbackListener().charCallback(window, character);
        });

        //Set cursor pos callback
        glfwSetCursorPosCallback(windowID, (long window, double x, double y) -> {
            ClientBase.getInstance().getInputCallbackListener().cursorPosCallback(window, x, y);
        });

        //Set Mouse Scroll callback
        glfwSetScrollCallback(windowID, (window, deltaX, deltaY) -> {
            ClientBase.getInstance().getInputCallbackListener().scrollCallback(window, deltaX, deltaY);
        });

        //Center the window on the primary monitor
        //TODO allow the createWindow method to configure this somehow
        videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        glfwSetWindowPos(windowID, (videoMode.width() - properties.getWidth()) / 2, (videoMode.height() - properties.getHeight()) / 2);

        //Update the window size variables based on post-init dimensions before showing the window
        try (MemoryStack frame = MemoryStack.stackPush()) {
            IntBuffer framebufferSize = frame.mallocInt(2);
            nglfwGetFramebufferSize(windowID, memAddress(framebufferSize), memAddress(framebufferSize) + 4);
            properties.resize(framebufferSize.get(0), framebufferSize.get(1));
        }

        //Set the window title
        glfwSetWindowTitle(windowID, properties.getTitle());

        //Show the window
        glfwShowWindow(windowID);

        //Start the window update loop
        threads.get(windowID).start();

        return windowID;
    }

    /**
     * Destroys the specified window, must be called from the main thread
     * @param thread The thread which will be destroyed
     */
    private void destroyWindow(WindowThread thread) {
        thread.destroyWindow();
        //Prevent an IllegalStateException on last destroyed window
        if (threads.size() > 1) {
            threads.remove(thread);
        }
    }

    /**
     * @return Whether this window manager has any alive windows
     */
    private boolean hasAliveWindow() {
        for (WindowThread thread : threads.values()) {
            if (!thread.quit) return true;
        }
        return false;
    }

    /**
     * Queues the specified window for destruction when it is safe to do so
     * @param thread The thread which you wish to destroy
     */
    public void queueDestroyWindow(WindowThread thread) {
        windowsToDestroy.add(thread);
    }

    /**
     * @param windowHandle the glfw window handle that you wish to retrieve properties for
     * @return The {@link WindowProperties} of the specified window
     */
    public WindowProperties getPropertiesFromWindow(long windowHandle) {
        return threads.get(windowHandle).getProperties();
    }

    /**
     * @return A list of window handles for all currently open windows controlled by TVE
     */
    public List<Long> getAllOpenWindows() {
        return threads.values().stream().map(WindowThread::getWindowHandle).collect(Collectors.toList());
    }

    /**
     * @return The window handle of (or -1) the currently active window
     */
    public long getFocusedWindow() {
        for (WindowThread thread : threads.values()) {
            if (thread.getProperties().isFocused()) return thread.getWindowHandle();
        }
        return -1;
    }

    /**
     * @return The window handle of (or -1) the currently moused over window
     */
    public long getMousedOverWindow() {
        for (WindowThread thread : threads.values()) {
            if (thread.getProperties().isMousedOver()) return thread.getWindowHandle();
        }
        return -1;
    }

    /**
     * Focuses the specified window
     * @param window The glfw handle to the window which is desired to be focused
     */
    public void focusWindow(long window) {
        glfwFocusWindow(window);
    }

    /**
     * Closes the currently focused window
     */
    public void closeFocusedWindow() {
        threads.get(getFocusedWindow()).destroyThread();
    }

    /**
     * Closes the currently moused over window
     */
    public void closeMousedOverWindow() {
        threads.get(getMousedOverWindow()).destroyThread();
    }
}
