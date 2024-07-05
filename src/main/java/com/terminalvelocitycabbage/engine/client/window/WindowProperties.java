package com.terminalvelocitycabbage.engine.client.window;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.materials.TextureCache;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.registry.Identifier;

/**
 * This class defines the current properties of a window
 */
public class WindowProperties {

    private int width;
    private int height;
    private String title;
    private boolean focused;
    private boolean mousedOver;
    private boolean resized;

    Identifier initialScene;
    Scene activeScene;

    public WindowProperties() {
        this(600, 400, "Default Title", null);
    }

    public WindowProperties(WindowProperties properties) {
        this(
                properties.getWidth(),
                properties.getHeight(),
                properties.getTitle(),
                properties.getInitialScene());
    }

    public WindowProperties(int width, int height, String title, Identifier initialSceneIdentifier) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.initialScene = initialSceneIdentifier;
    }

    /**
     * @return The width of this window
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return The height of this window in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return The current title of this window
     */
    public String getTitle() {
        return title;
    }

    /**
     * Updates the current title of the window as displayed on it's titlebar
     * @param title The String that this window title shall be changed to
     * @return These window properties
     */
    //TODO this should probably downcall to the window and update it's title, but it does not
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return Whether this window is currently the active window
     */
    public boolean isFocused() {
        return focused;
    }

    /**
     * @param focused Sets the state of this window to focused if it is
     */
    protected void setFocused(boolean focused) {
        this.focused = focused;
    }

    /**
     * @return Whether this mouse is currently being moused over
     */
    public boolean isMousedOver() {
        return mousedOver;
    }

    /**
     * @param mousedOver Sets the state of this window to the one which is mouse over if it is
     */
    protected void setMousedOver(boolean mousedOver) {
        this.mousedOver = mousedOver;
    }

    public void resize(int width, int height) {
        this.resized = true;
        this.width = width;
        this.height = height;
    }

    public void endFrame() {
        this.resized = false;
    }

    public boolean isResized() {
        return resized;
    }

    public void init(Identifier initialScene) {
        activeScene = ClientBase.getInstance().getSceneRegistry().get(initialScene);
        activeScene.setTextureCache(new TextureCache());
        activeScene.init();
    }

    public void setScene(Identifier sceneIdentifier) {
        var client = ClientBase.getInstance();
        TextureCache textureCache;
        if (activeScene != null) {
            textureCache = activeScene.getTextureCache();
            activeScene.cleanup();
            client.getManager().freeNonPersistentEntities();
        } else {
            textureCache = new TextureCache();
        }
        activeScene = client.getSceneRegistry().get(sceneIdentifier);
        activeScene.setTextureCache(textureCache);
        activeScene.init();
    }

    public Scene getActiveScene() {
        return activeScene;
    }

    public Identifier getInitialScene() {
        return initialScene;
    }
}
