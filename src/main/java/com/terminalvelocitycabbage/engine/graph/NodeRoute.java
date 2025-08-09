package com.terminalvelocitycabbage.engine.graph;

import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.state.StateHandler;
import org.lwjgl.opengl.GLCapabilities;

import java.util.function.BiPredicate;

public non-sealed class NodeRoute implements GraphNode {

    boolean initialized = false;
    BiPredicate<GLCapabilities, StateHandler> routePredicate;
    RenderGraph.RenderPath.Config mainNodeConfig;
    RenderGraph.RenderPath.Config backupNodeConfig;
    RenderGraph.RenderPath mainNode;
    RenderGraph.RenderPath backupNode;

    public NodeRoute(BiPredicate<GLCapabilities, StateHandler> capabilities, RenderGraph.RenderPath.Config mainNode, RenderGraph.RenderPath.Config backupNode) {
        this.routePredicate = capabilities;
        this.mainNodeConfig = mainNode;
        this.backupNodeConfig = backupNode;
    }

    /**
     * @param capabilities The GL capabilities of this device (can be used to determine the route)
     * @param stateHandler
     * @return The {@link GraphNode} that should be taken
     */
    public RenderGraph.RenderPath evaluate(GLCapabilities capabilities, StateHandler stateHandler) {

        if (!initialized) {
            Log.crash("Can't evaluate uninitialized Node Route");
        }

        if (routePredicate.test(capabilities, stateHandler)) {
            return getMainNode();
        }
        return getBackupNode();
    }

    /**
     * @return The {@link GraphNode} to take if the predicate is true
     */
    public RenderGraph.RenderPath getMainNode() {
        return mainNode;
    }

    /**
     * @return The {@link GraphNode} to take if the predicate is false
     */
    public RenderGraph.RenderPath getBackupNode() {
        return backupNode;
    }

    /**
     * Initializes this route for use in a render graph
     * @param renderGraph The render graph that this route belongs to
     */
    public void init(RenderGraph renderGraph) {
        mainNode = mainNodeConfig.build(renderGraph);
        backupNode = backupNodeConfig.build(renderGraph);
        initialized = true;
    }
}
