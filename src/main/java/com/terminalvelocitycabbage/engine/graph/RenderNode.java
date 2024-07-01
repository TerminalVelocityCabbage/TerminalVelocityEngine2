package com.terminalvelocitycabbage.engine.graph;

import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgram;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;

/**
 * A node for an {@link RenderGraph}, specifically for executing code that draws to the screen.
 */
public abstract non-sealed class RenderNode implements GraphNode {

    public abstract void executeRenderStage(RenderGraph renderGraph, WindowProperties properties, long deltaTime, ShaderProgram shaderProgram);

}
