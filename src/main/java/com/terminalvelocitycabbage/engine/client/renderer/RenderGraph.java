package com.terminalvelocitycabbage.engine.client.renderer;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.graph.GraphNode;
import com.terminalvelocitycabbage.engine.graph.NodeRoute;
import com.terminalvelocitycabbage.engine.graph.RenderNode;
import com.terminalvelocitycabbage.engine.graph.Routine;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.ClassUtils;
import com.terminalvelocitycabbage.engine.util.Toggle;
import com.terminalvelocitycabbage.engine.util.touples.Pair;
import com.terminalvelocitycabbage.templates.events.RenderGraphStageExecutionEvent;
import org.lwjgl.opengl.GLCapabilities;

import javax.management.ReflectionException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class RenderGraph {

    private boolean initialized;
    private GLCapabilities capabilities;
    private final RenderPath renderPath;
    private final Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes;

    private RenderGraph(RenderPath.Builder renderPathBuilder) {
        this.initialized = false;
        RenderPath renderPath1 = renderPathBuilder.build(this);
        this.renderPath = renderPath1;
        this.graphNodes = renderPath1.getNodes();
    }

    public void init(GLCapabilities capabilities) {
        initialized = true;
        this.capabilities = capabilities;
    }

    /**
     * @param identifier The identifier of the node you want to retrieve from this graph
     * @return The graph node
     */
    public GraphNode getNode(Identifier identifier) {
        return graphNodes.get(identifier).getValue1();
    }

    /**
     * Pauses the specified node of this renderer, when paused a node will be skipped in this graph's render pass
     * @param nodeIdentifier the {@link Identifier} for the node that you want to resume
     */
    public void pauseNode(Identifier nodeIdentifier) {
        graphNodes.get(nodeIdentifier).getValue0().disable();
    }

    /**
     * Resumes or "un-pauses" the specified node of this renderer
     * @param nodeIdentifier the {@link Identifier} for the node that you want to resume
     */
    public void resumeNode(Identifier nodeIdentifier) {
        graphNodes.get(nodeIdentifier).getValue0().enable();
    }

    /**
     * Returns weather this node is enabled or not
     * @param nodeIdentifier the {@link Identifier} for the node that you want to resume
     * @return if this node is enabled
     */
    public boolean nodeEnabled(Identifier nodeIdentifier) {
        return graphNodes.get(nodeIdentifier).getValue0().getStatus();
    }

    public void render(WindowProperties windowProperties, long deltaTime) {

        if (!initialized) Log.error("Tried to render before render graph was initialized");

        renderPath.render(windowProperties, deltaTime);
    }

    private void addNodes(Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes1) {
        graphNodes.putAll(graphNodes1);
    }

    public GLCapabilities getCapabilities() {
        return capabilities;
    }

    public void cleanup() {

    }

    /**
     * @return a new instance of {@link RenderPath.Builder} for use in configuring a new Render Graph.
     */
    public static RenderPath.Builder builder() {
        return new RenderPath.Builder();
    }

    public static class RenderPath {

        private final RenderGraph renderGraph;
        private final Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes;

        private RenderPath(RenderGraph graph, Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes) {
            this.renderGraph = graph;
            this.graphNodes = graphNodes;
            graph.addNodes(graphNodes);
        }

        /**
         * @param windowProperties The current snapshot of the calling window's properties
         * @param deltaTime The time passed since the last frame was started
         */
        public void render(WindowProperties windowProperties, long deltaTime) {
            graphNodes.forEach((identifier, graphNode) -> {
                boolean enabled = renderGraph.nodeEnabled(identifier);
                //Publish an event before this GraphNode so mods can inject their own logic into these renderers
                ClientBase.getInstance().getEventDispatcher().dispatchEvent(new RenderGraphStageExecutionEvent(RenderGraphStageExecutionEvent.pre(identifier), windowProperties, deltaTime, enabled));
                //Execute all nodes in the graph
                if (enabled && graphNode != null) {
                    switch (graphNode.getValue1()) {
                        case Routine routine -> routine.update(ClientBase.getInstance().getManager(), ClientBase.getInstance().getEventDispatcher()); //We assume that the server is not rendering anything
                        case RenderNode renderNode -> renderNode.executeRenderStage(windowProperties.getActiveScene(), windowProperties, deltaTime);
                        case NodeRoute nodeRoute -> nodeRoute.evaluate(renderGraph.capabilities).render(windowProperties, deltaTime);
                    }
                }
                //Publish an event before this GraphNode so mods can inject their own logic into these renderers
                ClientBase.getInstance().getEventDispatcher().dispatchEvent(new RenderGraphStageExecutionEvent(RenderGraphStageExecutionEvent.post(identifier), windowProperties, deltaTime, enabled));
            });
        }

        public Map<Identifier, Pair<Toggle, ? extends GraphNode>> getNodes() {
            return graphNodes;
        }

        public static class Builder {

            private final Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes;

            private Builder() {
                graphNodes = new HashMap<>();
            }

            public Builder addRoutineNode(Identifier identifier, Routine routine) {
                return addRoutineNode(identifier, routine, true);
            }

            public Builder addRoutineNode(Identifier identifier, Routine routine, boolean automaticallyEnable) {
                graphNodes.put(identifier, new Pair<>(new Toggle(automaticallyEnable), routine));
                return this;
            }

            /**
             * Adds a node to this render graph and automatically enables it
             * @param identifier the {@link Identifier} that corresponds to this node of the renderGraph
             * @param graphNode the node to be added to this graph
             * @return this Builder (for easy changing of methods)
             */
            public Builder addRenderNode(Identifier identifier, Class<? extends RenderNode> graphNode, ShaderProgramConfig config) {
                return addRenderNode(identifier, graphNode, config, true);
            }

            /**
             * Adds a node to this render graph and allows you to specify whether to enable it by default or not
             * useful for nodes that don't always get used or on nodes that don't need to run on the first iteration.
             * @param identifier the {@link Identifier} that corresponds to this node of the renderGraph
             * @param renderNode the node to be added to this graph
             * @param automaticallyEnable a boolean to represent if this node should be enabled or paused on initialization
             * @return this Builder (for easy changing of methods)
             */
            public Builder addRenderNode(Identifier identifier, Class<? extends RenderNode> renderNode, ShaderProgramConfig config, boolean automaticallyEnable) {
                try {
                    graphNodes.put(identifier, new Pair<>(new Toggle(automaticallyEnable), ClassUtils.createInstance(renderNode, config)));
                } catch (ReflectionException e) {
                    Log.crash("Could not add node " + identifier + " to graph node " + renderNode, new RuntimeException(e));
                }
                return this;
            }

            /**
             * A conditional node executor, if the predicate returns true the default node will be chosen and if false the backup node
             * @param identifier The identifier of this GraphNode
             * @param capabilitiesPredicate A predicate which determines which route to take in the graph
             * @param defaultNode The node progressed to if the predicate is true
             * @param backupNode The node progressed to if the predicate is false
             * @return this Builder (for easy changing of methods)
             */
            public Builder route(Identifier identifier, Predicate<GLCapabilities> capabilitiesPredicate, RenderPath defaultNode, RenderPath backupNode) {
                graphNodes.put(identifier, new Pair<>(new Toggle(true), new NodeRoute(capabilitiesPredicate, defaultNode, backupNode)));
                return this;
            }

            /**
             * @return A new {@link RenderGraph} instance generated from this builder.
             */
            public RenderPath build(RenderGraph renderGraph) {
                return new RenderPath(renderGraph, graphNodes);
            }

        }
    }

    public boolean isInitialized() {
        return initialized;
    }
}
