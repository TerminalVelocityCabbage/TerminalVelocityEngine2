package com.terminalvelocitycabbage.engine.graph;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.Projection;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.Mesh;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.client.ui.Style;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.util.HeterogeneousMap;
import com.terminalvelocitycabbage.templates.meshes.SquareDataMesh;

import static org.lwjgl.opengl.GL11.*;

public abstract class UIRenderNode extends RenderNode {

    static final Projection PROJECTION = new Projection(Projection.Type.ORTHOGONAL, 0, 100);

    public static final VertexFormat UI_ELEMENT_MESH_FORMAT = VertexFormat.builder()
            .addElement(VertexAttribute.XYZ_POSITION)
            .addElement(VertexAttribute.RGB_COLOR)
            .addElement(VertexAttribute.UV)
            .build();

    static final Mesh BOX_MESH = new Mesh(UI_ELEMENT_MESH_FORMAT, new SquareDataMesh());

    public UIRenderNode(ShaderProgramConfig shaderProgramConfig) {
        super(shaderProgramConfig);
    }

    public abstract void drawUIElements(Scene scene);

    @Override
    public void execute(Scene scene, WindowProperties properties, HeterogeneousMap renderConfig, long deltaTime) {

        //Set the shader up for rendering
        var shaderProgram = getShaderProgram();
        if (properties.isResized()) PROJECTION.updateProjectionMatrix(properties.getWidth(), properties.getHeight());
        shaderProgram.bind();
        shaderProgram.getUniform("textureSampler").setUniform(0);
        shaderProgram.getUniform("projectionMatrix").setUniform(PROJECTION.getProjectionMatrix());

        if (glIsEnabled(GL_DEPTH_TEST)) {
            glDisable(GL_DEPTH_TEST);
            drawUIElements(scene);
            glEnable(GL_DEPTH_TEST);
        } else {
            drawUIElements(scene);
        }

        //Reset
        shaderProgram.unbind();
    }

    public void drawBox(Scene scene, Style style) {

        var texture = ClientBase.getInstance().getTextureCache().getTexture(style.getTextureIdentifier());
        texture.bind();

        shaderProgram.getUniform("modelMatrix").setUniform(style.getTransformation().getTransformationMatrix());

        BOX_MESH.render();

    }

}
