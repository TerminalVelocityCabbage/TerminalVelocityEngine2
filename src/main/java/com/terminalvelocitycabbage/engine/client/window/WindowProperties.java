package com.terminalvelocitycabbage.engine.client.window;

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

    Identifier renderer;

    public WindowProperties() {
        this.width = 600;
        this.height = 400;
        this.title = "Default Title";
        this.renderer = null;
    }

    public WindowProperties(WindowProperties properties) {
        this.width = properties.getWidth();
        this.height = properties.getHeight();
        this.title = properties.getTitle();
        this.renderer = properties.getRenderGraph();
    }

    public WindowProperties(int width, int height, String title, Identifier renderer) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.renderer = renderer;
    }

    /**
     * @return The width of this window
     */
    public int getWidth() {
        return width;
    }

    /**
     * Updates the width of this window
     * @param width The width in pixels that this window should be changed to
     */
    //TODO expose this and make it do something
    protected void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return The height of this window in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Updates the height of this window
     * @param height The height in pixels that this window should be changed to
     */
    //TODO expose this and make it do something
    protected void setHeight(int height) {
        this.height = height;
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
     * @return The Identifier that refers to the current active renderer of this window
     */
    public Identifier getRenderGraph() {
        return renderer;
    }

    /**
     * Updates the current active renderer for this window
     * @param renderer The identifier which you wish this window to update
     */
    //TODO this should likely invoke some renderer lifecycle events for setup and destroy
    public void setRenderer(Identifier renderer) {
        this.renderer = renderer;
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

    public void setResized(boolean resized) {
        this.resized = resized;
    }

    public boolean isResized() {
        return resized;
    }
}
