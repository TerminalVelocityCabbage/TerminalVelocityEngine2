package com.terminalvelocitycabbage.engine.graph;

import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;

/**
 * This is just a utility class for controlling the types of classes that can be registered to a {@link RenderGraph}.
 */
sealed public interface GraphNode permits NodeRoute, RenderNode, Routine {}
