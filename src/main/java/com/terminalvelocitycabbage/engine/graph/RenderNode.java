package com.terminalvelocitycabbage.engine.graph;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.Framebuffer;
import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.client.renderer.TargetProperties;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgram;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.HeterogeneousMap;

import static org.lwjgl.opengl.GL11.glViewport;

/**
 * A node for an {@link RenderGraph}, specifically for executing code that draws to a target.
 */
public abstract non-sealed class RenderNode implements GraphNode {

    boolean recompileShaders = false;
    final ShaderProgramConfig shaderProgramConfig;
    ShaderProgram shaderProgram;
    Identifier targetFramebufferId;
    private TargetProperties currentFboProperties;

    public RenderNode(ShaderProgramConfig shaderProgramConfig) {
        this.shaderProgramConfig = shaderProgramConfig;
    }

    public void init(RenderGraph renderGraph) {
        //Compile this shader program now that this renderer is ready to go (if needed)
        if (shaderProgram == null && shaderProgramConfig != null && !shaderProgramConfig.isEmpty()) {
            shaderProgram = ShaderProgram.of(shaderProgramConfig);
            recompileShaders = false;
        }
    }

    /**
     * DO NOT OVERRIDE THIS METHOD
     *
     * @param scene The scene that this stage is rendering
     * @param properties The properties of the currently rendered to target
     * @param renderConfig This render graphs render config
     * @param deltaTime The time since the last render operation
     */
    public void executeRenderStage(Scene scene, TargetProperties properties, HeterogeneousMap renderConfig, long deltaTime) {

        //Wipe the current shader program for re-compilation
        if (recompileShaders) {
            shaderProgram.cleanup();
            shaderProgram = ShaderProgram.of(shaderProgramConfig);
            recompileShaders = false;
        }

        TargetProperties currentProperties = properties;
        Framebuffer targetFramebuffer = targetFramebufferId == null ? null : ClientBase.getInstance().getFramebufferRegistry().get(targetFramebufferId);
        if (targetFramebuffer != null) {
            targetFramebuffer.init();
            targetFramebuffer.bind();
            glViewport(0, 0, targetFramebuffer.getWidth(), targetFramebuffer.getHeight());
            if (currentFboProperties == null) {
                currentFboProperties = new TargetProperties(targetFramebuffer.getWidth(), targetFramebuffer.getHeight(), targetFramebuffer.isResized(), scene, targetFramebuffer);
            } else {
                currentFboProperties.update(targetFramebuffer.getWidth(), targetFramebuffer.getHeight(), targetFramebuffer.isResized(), scene, targetFramebuffer);
            }
            currentProperties = currentFboProperties;
        }

        render(scene, currentProperties, renderConfig, deltaTime);

        if (targetFramebuffer != null) {
            targetFramebuffer.unbind();
            glViewport(0, 0, properties.getWidth(), properties.getHeight());
            targetFramebuffer.resetResized();
        }
    }

    /**
     * @return the {@link Identifier} target of this node (if it has one)
     */
    public Identifier getTargetFramebufferId() {
        return targetFramebufferId;
    }

    /**
     * @param targetFramebufferId the {@link Identifier} of the {@link Framebuffer} that this node should draw to (set to null for the screen)
     */
    public void setTargetFramebufferId(Identifier targetFramebufferId) {
        this.targetFramebufferId = targetFramebufferId;
    }

    /**
     * The method that should be overriden to assign logic to this stage in the render graph
     *
     * @param scene The scene that this stage is rendering
     * @param properties The properties of the currently rendered to target
     * @param renderConfig This render graphs render config
     * @param deltaTime The time since the last render operation
     */
    public abstract void render(Scene scene, TargetProperties properties, HeterogeneousMap renderConfig, long deltaTime);

    /**
     * Marks the shaders of this node to be recompiled at the start of the next render stage
     */
    public void recompileShaders() {
        recompileShaders = true;
    }

    /**
     * @return The currently compiled shader program of this node
     */
    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }

    /**
     * @return The current shader program config of this node
     */
    public ShaderProgramConfig getShaderProgramConfig() {
        return shaderProgramConfig;
    }
}
