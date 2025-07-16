package com.terminalvelocitycabbage.engine.graph;

import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import org.lwjgl.opengl.GLCapabilities;

import java.util.function.Predicate;

public non-sealed class NodeRoute implements GraphNode {

    Predicate<GLCapabilities> capabilitiesPredicate;
    RenderGraph.RenderPath mainNode;
    RenderGraph.RenderPath backupNode;

    public NodeRoute(Predicate<GLCapabilities> capabilities, RenderGraph.RenderPath mainNode, RenderGraph.RenderPath backupNode) {
        this.capabilitiesPredicate = capabilities;
        this.mainNode = mainNode;
        this.backupNode = backupNode;
    }

    /**
     * @param capabilities The GL capabilities of this device (can be used to determine the route)
     * @return The {@link GraphNode} that should be taken
     */
    public RenderGraph.RenderPath evaluate(GLCapabilities capabilities) {
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
}
