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

    public abstract void execute(Scene scene, WindowProperties properties, long deltaTime);

    public void recompileShaders() {
        recompileShaders = true;
    }

    public ShaderProgram getShaderProgram() {
        return shaderProgram;
    }
}
