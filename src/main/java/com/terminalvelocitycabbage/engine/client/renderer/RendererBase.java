package com.terminalvelocitycabbage.engine.client.renderer;

import com.terminalvelocitycabbage.engine.client.window.WindowProperties;

public abstract class RendererBase {

    private int rendererId;

    public abstract void init(WindowProperties properties, long windowHandle);
    public abstract void update(WindowProperties properties, long deltaTime);
    public abstract void destroy();

    public int getRendererId() {
        return rendererId;
    }

    public void setRendererId(int rendererId) {
        this.rendererId = rendererId;
    }
}
