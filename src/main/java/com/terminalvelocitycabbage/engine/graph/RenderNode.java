package com.terminalvelocitycabbage.engine.graph;

import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgram;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;

/**
 * A node for an {@link RenderGraph}, specifically for executing code that draws to the screen.
 */
public abstract non-sealed class RenderNode implements GraphNode {

    boolean recompileShaders = false;
    ShaderProgramConfig shaderProgramConfig;
    ShaderProgram shaderProgram;

    public RenderNode(ShaderProgramConfig shaderProgramConfig) {
        this.shaderProgramConfig = shaderProgramConfig;
    }

    /**
     * DO NOT OVERRIDE THIS METHOD
     *
     * @param scene The scene that this stage is rendering
     * @param properties The properties of the currently rendered to window
     * @param deltaTime The time since the last render operation
     */
    public void executeRenderStage(Scene scene, WindowProperties properties, long deltaTime) {

        //Wipe the current shader program for re-compilation
        if (recompileShaders) {
            shaderProgram.cleanup();
            shaderProgram = null;
        }

        //Compile this shader program now that this renderer is ready to do (if needed)
        if (shaderProgram == null && shaderProgramConfig != null) {
            shaderProgram = ShaderProgram.of(shaderProgramConfig);
            recompileShaders = false;
        }

        execute(scene, properties, deltaTime);
    }

    /**
     * The method that should be overriden to assign logic to this stage in the render graph
     *
     * @param scene The scene that this stage is rendering
     * @param properties The properties of the currently rendered to window
     * @param deltaTime The time since the last render operation
     */
    public abstract void execute(Scene scene, WindowProperties properties, long deltaTime);

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
}
