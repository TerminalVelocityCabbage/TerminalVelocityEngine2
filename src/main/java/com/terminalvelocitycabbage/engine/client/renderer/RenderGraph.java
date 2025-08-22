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
import com.terminalvelocitycabbage.engine.state.StateHandler;
import com.terminalvelocitycabbage.engine.util.ClassUtils;
import com.terminalvelocitycabbage.engine.util.HeterogeneousMap;
import com.terminalvelocitycabbage.engine.util.Toggle;
import com.terminalvelocitycabbage.engine.util.touples.Pair;
import com.terminalvelocitycabbage.templates.events.RenderGraphStageExecutionEvent;
import org.lwjgl.opengl.GLCapabilities;

import javax.management.ReflectionException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiPredicate;

public class RenderGraph {

    //If this RenderGraph is currently in use
    private boolean initialized;
    //The GL Capabilities of the render device
    private GLCapabilities capabilities;
    //The root path for this render graph
    private final RenderPath renderPath;
    //A list of all graph nodes on the root path or child paths
    private final Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes;
    //A map of configurable values for use in this render graph
    private final HeterogeneousMap renderConfig;

    public RenderGraph(RenderPath.Config renderPathBuilder) {
        this.initialized = false;
        this.renderConfig = new HeterogeneousMap();
        this.graphNodes = new HashMap<>();
        this.renderPath = renderPathBuilder.build(this);
        for (Pair<Toggle, ? extends GraphNode> togglePair : graphNodes.values()) {
            if (togglePair.getValue1() instanceof NodeRoute route) {
                route.init(this);
            }
        }
    }

    /**
     * initializes this RenderGraph for use. This is called automatically when the window is shown
     * @param capabilities the gl capabilities of this device
     */
    public void init(GLCapabilities capabilities) {

        //Validate shaders match up with their configs
        graphNodes.forEach((identifier, pair) -> {
            if (pair.getValue1() instanceof RenderNode renderNode) {
                renderNode.init();
                renderNode.getShaderProgramConfig().validateVertexShader();
            }
        });

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

    /**
     * Calls the render method on the root render path which passes it on down the line depending on the conditional routes
     * @param windowProperties The current snapshot of the calling window's properties
     * @param deltaTime The time passed since the last frame was started
     */
    public void render(WindowProperties windowProperties, long deltaTime) {
        if (!initialized) Log.error("Tried to render before render graph was initialized");
        renderPath.render(windowProperties, deltaTime);
    }

    public void cleanup() {

    }

    public HeterogeneousMap getRenderConfig() {
        return renderConfig;
    }

    public static class RenderPath {

        private final RenderGraph renderGraph;
        private final Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes;

        private RenderPath(RenderGraph graph, Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes) {
            this.renderGraph = graph;
            this.graphNodes = graphNodes;
            graph.graphNodes.putAll(graphNodes);
        }

        /**
         * @return A new {@link RenderPath.Config} builder which allows you to define the nodes and routes for this path
         */
        public static Config builder() {
            return new Config();
        }

        //Renders this path
        public void render(WindowProperties windowProperties, long deltaTime) {
            graphNodes.forEach((identifier, graphNode) -> {
                boolean enabled = renderGraph.nodeEnabled(identifier);
                //Publish an event before this GraphNode so mods can inject their own logic into these renderers
                ClientBase.getInstance().getEventDispatcher().dispatchEvent(new RenderGraphStageExecutionEvent(RenderGraphStageExecutionEvent.pre(identifier), windowProperties, deltaTime, enabled));
                //Execute all nodes in the graph
                if (enabled && graphNode != null) {
                    switch (graphNode.getValue1()) {
                        case Routine routine -> routine.update(ClientBase.getInstance().getManager(), ClientBase.getInstance().getEventDispatcher(), deltaTime); //We assume that the server is not rendering anything
                        case RenderNode renderNode -> renderNode.executeRenderStage(windowProperties.getActiveScene(), windowProperties, renderGraph.getRenderConfig(), deltaTime);
                        case NodeRoute nodeRoute -> nodeRoute.evaluate(renderGraph.capabilities, ClientBase.getInstance().getStateHandler()).render(windowProperties, deltaTime);
                    }
                }
                //Publish an event before this GraphNode so mods can inject their own logic into these renderers
                ClientBase.getInstance().getEventDispatcher().dispatchEvent(new RenderGraphStageExecutionEvent(RenderGraphStageExecutionEvent.post(identifier), windowProperties, deltaTime, enabled));
            });
        }

        public static class Config {

            private final Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes;
            private final HeterogeneousMap renderConfig;

            public static final RenderGraph.RenderPath.Config EMPTY_ROUTE = RenderGraph.RenderPath.builder();

            private Config() {
                graphNodes = new LinkedHashMap<>();
                renderConfig = new HeterogeneousMap();
            }

            /**
             * @param identifier the {@link Identifier} that corresponds to this node of the renderGraph
             * @param routine the routine to be executed at this stage in the graph
             * @return this Builder (for easy changing of methods)
             */
            public Config addRoutineNode(Identifier identifier, Routine routine) {
                return addRoutineNode(identifier, routine, true);
            }

            /**
             * @param identifier the {@link Identifier} that corresponds to this node of the renderGraph
             * @param routine the routine to be executed at this stage in the graph
             * @param automaticallyEnable a boolean to represent if this node should be enabled or paused on initialization
             * @return this Builder (for easy changing of methods)
             */
            public Config addRoutineNode(Identifier identifier, Routine routine, boolean automaticallyEnable) {
                graphNodes.put(identifier, new Pair<>(new Toggle(automaticallyEnable), routine));
                return this;
            }

            /**
             * Adds a node to this render graph and automatically enables it
             * @param identifier the {@link Identifier} that corresponds to this node of the renderGraph
             * @param graphNode the node to be added to this graph
             * @return this Builder (for easy changing of methods)
             */
            public Config addRenderNode(Identifier identifier, Class<? extends RenderNode> graphNode, ShaderProgramConfig config) {
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
            public Config addRenderNode(Identifier identifier, Class<? extends RenderNode> renderNode, ShaderProgramConfig config, boolean automaticallyEnable) {
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
             * @param routePredicate A predicate which determines which route to take in the graph
             * @param defaultNode The node progressed to if the predicate is true
             * @param backupNode The node progressed to if the predicate is false
             * @return this Builder (for easy changing of methods)
             */
            public Config route(Identifier identifier, BiPredicate<GLCapabilities, StateHandler> routePredicate, Config defaultNode, Config backupNode) {
                graphNodes.put(identifier, new Pair<>(new Toggle(true), new NodeRoute(routePredicate, defaultNode, backupNode)));
                return this;
            }

            /**
             * A conditional node executor, if the predicate returns true the default node will be chosen and if false nothing will happen
             * @param identifier The identifier of this GraphNode
             * @param routePredicate A predicate which determines which route to take in the graph
             * @param defaultNode The node progressed to if the predicate is true
             * @return this Builder (for easy changing of methods)
             */
            public Config route(Identifier identifier, BiPredicate<GLCapabilities, StateHandler> routePredicate, Config defaultNode) {
                graphNodes.put(identifier, new Pair<>(new Toggle(true), new NodeRoute(routePredicate, defaultNode, EMPTY_ROUTE)));
                return this;
            }

            /**
             * @return A new {@link RenderGraph} instance generated from this builder.
             */
            public RenderPath build(RenderGraph renderGraph) {
                renderGraph.getRenderConfig().addAll(renderConfig);
                return new RenderPath(renderGraph, graphNodes);
            }

            /**
             * @param configKey The {@link HeterogeneousMap.Key} key of ths associated config value for this graph
             * @param configValue The value associated with this config value
             * @param <T> The type of this config value
             * @return this Builder (for easy chaining of methods)
             */
            public <T> Config configure(HeterogeneousMap.Key<T> configKey, T configValue) {
                renderConfig.set(configKey, configValue);
                return this;
            }
        }
    }

    public boolean isInitialized() {
        return initialized;
    }
}
