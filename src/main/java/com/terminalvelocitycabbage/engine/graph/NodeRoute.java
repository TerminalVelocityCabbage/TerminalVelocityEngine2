package com.terminalvelocitycabbage.engine.graph;

import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.debug.Log;
import org.lwjgl.opengl.GLCapabilities;

import java.util.function.Predicate;

public non-sealed class NodeRoute implements GraphNode {

    boolean initialized = false;
    Predicate<GLCapabilities> capabilitiesPredicate;
    RenderGraph.RenderPath.Config mainNodeConfig;
    RenderGraph.RenderPath.Config backupNodeConfig;
    RenderGraph.RenderPath mainNode;
    RenderGraph.RenderPath backupNode;

    public NodeRoute(Predicate<GLCapabilities> capabilities, RenderGraph.RenderPath.Config mainNode, RenderGraph.RenderPath.Config backupNode) {
        this.capabilitiesPredicate = capabilities;
        this.mainNodeConfig = mainNode;
        this.backupNodeConfig = backupNode;
    }

    /**
     * @param capabilities The GL capabilities of this device (can be used to determine the route)
     * @return The {@link GraphNode} that should be taken
     */
    public RenderGraph.RenderPath evaluate(GLCapabilities capabilities) {

        if (!initialized) {
            Log.crash("Can't evaluate uninitialized Node Route");
        }

        if (capabilitiesPredicate.test(capabilities)) {
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

    public void init(RenderGraph renderGraph) {
        mainNode = mainNodeConfig.build(renderGraph);
        backupNode = backupNodeConfig.build(renderGraph);
        initialized = true;
    }
}
