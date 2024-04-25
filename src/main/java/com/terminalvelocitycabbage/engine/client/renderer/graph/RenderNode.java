package com.terminalvelocitycabbage.engine.client.renderer.graph;

import com.terminalvelocitycabbage.engine.client.window.WindowProperties;

public abstract non-sealed class RenderNode implements GraphNode {

    public abstract void executeRenderStage(WindowProperties properties, long deltaTime);

}
