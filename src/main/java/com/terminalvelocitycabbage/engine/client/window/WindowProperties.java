package com.terminalvelocitycabbage.engine.client.window;

import com.terminalvelocitycabbage.engine.client.renderer.RendererBase;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public class WindowProperties {

    private int width;
    private int height;
    private String title;

    Identifier renderer;
    int rendererId;

    public WindowProperties(WindowProperties properties) {
        this.width = properties.getWidth();
        this.height = properties.getHeight();
        this.title = properties.getTitle();
        this.renderer = properties.getRenderer();
    }

    public WindowProperties(int width, int height, String title, Identifier renderer) {
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

    public Identifier getRenderer() {
        return renderer;
    }

    public void setRenderer(Identifier renderer) {
        this.renderer = renderer;
    }

    public void setRendererID(int id) {
        rendererId = id;
    }

    public int getRendererId() {
        return rendererId;
    }
}
