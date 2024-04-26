package com.terminalvelocitycabbage.engine.client.renderer.graph;

/**
 * This is just a utility class for controlling the types of classes that can be registered to a {@link RenderGraph}.
 */
sealed interface GraphNode permits RenderNode, Routine {}
