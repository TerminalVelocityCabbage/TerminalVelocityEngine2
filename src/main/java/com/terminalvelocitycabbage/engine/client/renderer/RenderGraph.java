package com.terminalvelocitycabbage.engine.client.renderer;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgram;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.graph.GraphNode;
import com.terminalvelocitycabbage.engine.graph.RenderNode;
import com.terminalvelocitycabbage.engine.graph.Routine;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.ClassUtils;
import com.terminalvelocitycabbage.engine.util.Toggle;
import com.terminalvelocitycabbage.engine.util.touples.Pair;
import com.terminalvelocitycabbage.templates.events.RenderGraphStageExecutionEvent;

import javax.management.ReflectionException;
import java.util.HashMap;
import java.util.Map;

public class RenderGraph {

    private boolean initialized;
    private final ShaderProgramConfig shaderProgramConfig;
    private ShaderProgram compiledShaderProgram;
    private final Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes;
    private boolean recompileShaders;

    private RenderGraph(ShaderProgramConfig shaderProgram, Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes) {
        this.initialized = false;
        this.shaderProgramConfig = shaderProgram;
        this.graphNodes = graphNodes;
        this.recompileShaders = false;
    }

    public void init() {
        initialized = true;
    }

    public void cleanup() {

    }

    /**
     * @return a new instance of {@link RenderGraph.Builder} for use in configuring a new Render Graph.
     */
    public static Builder builder() {
        return new Builder();
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
     * @param windowProperties The current snapshot of the calling window's properties
     * @param deltaTime The time passed since the last frame was started
     */
    public void render(WindowProperties windowProperties, long deltaTime) {

        if (!initialized) Log.error("Tried to render before render graph was initialized");

        //Wipe the current shader program for re-compilation
        if (recompileShaders) {
            compiledShaderProgram.cleanup();
            compiledShaderProgram = null;
        }

        //Compile this shader program now that this renderer is ready to do (if needed)
        if (compiledShaderProgram == null && shaderProgramConfig != null) {
            compiledShaderProgram = ShaderProgram.of(shaderProgramConfig);
            recompileShaders = false;
        }

        graphNodes.forEach((identifier, graphNode) -> {
            if (graphNode.getValue1() == null) return;
            var enabled = graphNode.getValue0().getStatus();
            //Publish an event before this GraphNode so mods can inject their own logic into these renderers
            ClientBase.getInstance().getEventDispatcher().dispatchEvent(new RenderGraphStageExecutionEvent(RenderGraphStageExecutionEvent.pre(identifier), windowProperties, deltaTime, enabled));
            //Execute this graph node (whether it's a routine or a render node) if it's not paused
            if (enabled) {
                switch (graphNode.getValue1()) {
                    case Routine routine -> routine.update(ClientBase.getInstance().getManager()); //We assume that the server is not rendering anything
                    case RenderNode renderNode -> renderNode.executeRenderStage(windowProperties.getActiveScene(), windowProperties, deltaTime);
                }
            }
            //Publish an event before this GraphNode so mods can inject their own logic into these renderers
            ClientBase.getInstance().getEventDispatcher().dispatchEvent(new RenderGraphStageExecutionEvent(RenderGraphStageExecutionEvent.post(identifier), windowProperties, deltaTime, enabled));
        });
    }

    public static class Builder {

        ShaderProgramConfig shaderProgram;
        private final Map<Identifier, Pair<Toggle, ? extends GraphNode>> graphNodes;

        private Builder() {
            graphNodes = new HashMap<>();
        }

        /**
         * @param shaderProgram The shader program that this render graph will use to render objects
         * @return this Builder (for each chaining of methods)
         */
        public Builder shaderProgram(ShaderProgramConfig shaderProgram) {
            this.shaderProgram = shaderProgram;
            return this;
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
        public Builder addRenderNode(Identifier identifier, Class<? extends RenderNode> graphNode) {
            return addRenderNode(identifier, graphNode, true);
        }

        /**
         * Adds a node to this render graph and allows you to specify whether to enable it by default or not
         * useful for nodes that don't always get used or on nodes that don't need to run on the first iteration.
         * @param identifier the {@link Identifier} that corresponds to this node of the renderGraph
         * @param renderNode the node to be added to this graph
         * @param automaticallyEnable a boolean to represent if this node should be enabled or paused on initialization
         * @return this Builder (for easy changing of methods)
         */
        public Builder addRenderNode(Identifier identifier, Class<? extends RenderNode> renderNode, boolean automaticallyEnable) {
            try {
                graphNodes.put(identifier, new Pair<>(new Toggle(automaticallyEnable), ClassUtils.createInstance(renderNode)));
            } catch (ReflectionException e) {
                Log.crash("Could not add node " + identifier + " to graph node " + renderNode, new RuntimeException(e));
            }
            return this;
        }

        /**
         * @return A new {@link RenderGraph} instance generated from this builder.
         */
        public RenderGraph build() {
            return new RenderGraph(shaderProgram, graphNodes);
        }

    }

    public boolean isInitialized() {
        return initialized;
    }

    //TODO make this a part of a render node instead of the whole graph
    public ShaderProgram getShaderProgram() {
        return compiledShaderProgram;
    }

    public void recompileShaders() {
        recompileShaders = true;
    }
}
