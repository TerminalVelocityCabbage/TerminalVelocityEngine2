package com.terminalvelocitycabbage.engine.client.window;

import com.terminalvelocitycabbage.engine.client.renderer.RendererBase;

public class WindowProperties {

    private int width;
    private int height;
    private String title;

    RendererBase renderer;

    public WindowProperties(WindowProperties properties) {
        this.width = properties.getWidth();
        this.height = properties.getHeight();
        this.title = properties.getTitle();
        this.renderer = properties.getRenderer();
    }

    public WindowProperties(int width, int height, String title, RendererBase renderer) {
        this.width = width;
        this.height = height;
        this.title = title;
        this.renderer = renderer;
    }

    public int getWidth() {
        return width;
    }

    public WindowProperties setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public WindowProperties setHeight(int height) {
        this.height = height;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public WindowProperties setTitle(String title) {
        this.title = title;
        return this;
    }

    public RendererBase getRenderer() {
        return renderer;
    }

    public void setRenderer(RendererBase renderer) {
        this.renderer = renderer;
    }
}
