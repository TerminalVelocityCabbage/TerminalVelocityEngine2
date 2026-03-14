package com.terminalvelocitycabbage.engine.client.renderer;

import com.terminalvelocitycabbage.engine.client.scene.Scene;

/**
 * Defines the properties of a render target (Window or Framebuffer)
 */
public class TargetProperties {

    private int width;
    private int height;
    private boolean resized;
    private Scene scene;
    private Framebuffer framebuffer;

    public TargetProperties(int width, int height, boolean resized, Scene scene) {
        this(width, height, resized, scene, null);
    }

    public TargetProperties(int width, int height, boolean resized, Scene scene, Framebuffer framebuffer) {
        this.width = width;
        this.height = height;
        this.resized = resized;
        this.scene = scene;
        this.framebuffer = framebuffer;
    }

    public void update(int width, int height, boolean resized, Scene scene, Framebuffer framebuffer) {
        this.width = width;
        this.height = height;
        this.resized = resized;
        this.scene = scene;
        this.framebuffer = framebuffer;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isResized() {
        return resized;
    }

    public Scene getScene() {
        return scene;
    }

    public Framebuffer getFramebuffer() {
        return framebuffer;
    }
}
