package com.terminalvelocitycabbage.engine.client.renderer;

import com.terminalvelocitycabbage.engine.client.renderer.graph.RenderGraph;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;

public abstract class RendererBase {

    RenderGraph renderGraph;

    public RendererBase(RenderGraph renderGraph) {
        this.renderGraph = renderGraph;
    }

    public void init(WindowProperties properties, long windowHandle) {

    }

    public void render(WindowProperties properties, long deltaTime) {
        renderGraph.render(properties, deltaTime);
    }

    public void destroy() {

    }

    public RenderGraph getRenderGraph() {
        return renderGraph;
    }
}
