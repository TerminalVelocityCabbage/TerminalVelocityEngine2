package com.terminalvelocitycabbage.engine.client.renderer;

import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.ecs.Routine;

public abstract class RendererBase {

    Routine renderRoutine;

    public abstract void init(WindowProperties properties, long windowHandle);
    public abstract void update(WindowProperties properties, long deltaTime);
    public abstract void destroy();
}
