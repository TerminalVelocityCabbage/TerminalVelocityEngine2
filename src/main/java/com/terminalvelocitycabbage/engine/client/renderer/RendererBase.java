package com.terminalvelocitycabbage.engine.client.renderer;

import com.terminalvelocitycabbage.engine.client.window.WindowProperties;

public abstract class RendererBase {

    public abstract void init();
    public abstract void update(WindowProperties properties);
    public abstract void destroy();

}
