package com.terminalvelocitycabbage.engine.client.renderer.graph;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.ClassUtils;
import com.terminalvelocitycabbage.engine.util.Toggle;
import com.terminalvelocitycabbage.engine.util.touples.Pair;

import javax.management.ReflectionException;
import java.util.HashMap;
import java.util.Map;

public class RenderGraph {

    private final Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes;

    private RenderGraph(Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes) {
        this.graphNodes = graphNodes;
    }

    public static Builder builder() {
        return new Builder();
    }

    public void pauseNode(Identifier nodeIdentifier) {
        graphNodes.get(nodeIdentifier).getValue0().disable();
    }

    public void resumeNode(Identifier nodeIdentifier) {
        graphNodes.get(nodeIdentifier).getValue0().enable();
    }

    public void render(WindowProperties windowProperties, long deltaTime) {
        graphNodes.forEach((identifier, graphNode) -> {
            if (!graphNode.getValue0().getStatus()) return;
            switch (graphNode.getValue1()) {
                case Routine routine -> routine.update(ClientBase.getInstance().getManager()); //We assume that the server is not rendering anything
                case RenderNode renderNode -> renderNode.executeRenderStage(windowProperties, deltaTime);
            }
        });
    }

    public static class Builder {

        private final Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes;

        private Builder() {
            graphNodes = new HashMap<>();
        }

        public Builder addNode(Identifier identifier, Class<? extends GraphNode> graphNode) {
            return addNode(identifier, graphNode, true);
        }

        public Builder addNode(Identifier identifier, Class<? extends GraphNode> graphNode, boolean automaticallyEnable) {
            try {
                graphNodes.put(identifier, new Pair<>(new Toggle(automaticallyEnable), ClassUtils.createInstance(graphNode)));
            } catch (ReflectionException e) {
                Log.crash("Could not add node " + identifier + " to graph node " + graphNode, new RuntimeException(e));
            }
            return this;
        }

        public RenderGraph build() {
            return new RenderGraph(graphNodes);
        }

    }

}
