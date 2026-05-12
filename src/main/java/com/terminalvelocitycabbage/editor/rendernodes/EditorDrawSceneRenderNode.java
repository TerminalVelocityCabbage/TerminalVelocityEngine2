package com.terminalvelocitycabbage.editor.rendernodes;

import com.terminalvelocitycabbage.editor.ecs.components.EditorCameraComponent;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.model.Model;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.ecs.Entity;
import com.terminalvelocitycabbage.engine.graph.RenderNode;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.HeterogeneousMap;
import com.terminalvelocitycabbage.templates.ecs.components.*;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class EditorDrawSceneRenderNode extends RenderNode {

    public EditorDrawSceneRenderNode(ShaderProgramConfig shaderProgramConfig) {
        super(shaderProgramConfig);
    }

    @Override
    public void execute(Scene scene, WindowProperties properties, HeterogeneousMap renderConfig, long deltaTime) {

        var client = ClientBase.getInstance();
        var manager = client.getManager();

        var cameraEntity = getCamera();
        if (cameraEntity == null) return;

        var camera = cameraEntity.getComponent(CameraComponent.class);
        var transformation = cameraEntity.getComponent(TransformationComponent.class).getTransformation();

        var shaderProgram = getShaderProgram();
        if (properties.isResized()) camera.updateProjectionMatrix(properties.getWidth(), properties.getHeight());

        shaderProgram.bind();
        if (shaderProgram.getUniform("textureSampler") != null) shaderProgram.getUniform("textureSampler").setUniform(0);
        if (shaderProgram.getUniform("projectionMatrix") != null) shaderProgram.getUniform("projectionMatrix").setUniform(camera.getProjectionMatrix());
        if (shaderProgram.getUniform("viewMatrix") != null) shaderProgram.getUniform("viewMatrix").setUniform(camera.getViewMatrix(transformation));

        var lightEntity = manager.getFirstEntityWith(DirectionalLightComponent.class);
        if (lightEntity != null && shaderProgram.getConfig().getUniform("directionalLight") != null) {
            shaderProgram.getUniform("directionalLight").setUniform(lightEntity.getComponent(DirectionalLightComponent.class).getLight());
        }

        //Sort entities for efficient rendering (by texture then by model)
        List<Entity> entities = new ArrayList<>(manager.getEntitiesWith(ModelComponent.class, TransformationComponent.class));
        entities.sort(Comparator
                .comparingInt((Entity entity) -> client.getTextureCache().getTexture(client.getModelRegistry().get(entity.getComponent(ModelComponent.class).getModel()).textureIdentifier()).getTextureID())
                .thenComparing(entity -> entity.getComponent(ModelComponent.class).getModel().hashCode())
        );

        //Render entities
        Identifier lastTextureID = null;
        Identifier lastModelID = null;
        Model model;
        for (Entity entity : entities) {

            //Update the transformation to that of this entity
            shaderProgram.getUniform("modelMatrix").setUniform(entity.getComponent(TransformationComponent.class).getTransformationMatrix());

            //Handle animations
            var modelIdentifier = entity.getComponent(ModelComponent.class).getModel();
            model = client.getModelRegistry().get(modelIdentifier);
            if (model.skeleton() != null && shaderProgram.getConfig().getUniform("boneMatrices") != null) {
                Matrix4f[] matrices;
                if (entity.hasComponent(AnimationControllerComponent.class)) {
                    var animComp = entity.getComponent(AnimationControllerComponent.class);
                    matrices = animComp.getBoneMatrices(model);
                } else {
                    matrices = model.skeleton().bindPoseMatrices();
                }
                shaderProgram.getUniform("boneMatrices").setUniform(matrices);
            }

            //Early draw if this is the same model as the last entity (save on uploads)
            if (model.compiledMesh().getFormat().equals(shaderProgram.getConfig().getVertexFormat())) {
                if (modelIdentifier.equals(lastModelID)) {
                    model.draw();
                    continue;
                }

                lastModelID = modelIdentifier;

                //Optimization: only bind texture and mesh if they've changed since the last entity
                var textureIdentifier = model.textureIdentifier();
                if (textureIdentifier != null && !textureIdentifier.equals(lastTextureID)) {
                    model.bindTexture(client.getTextureCache());
                    lastTextureID = textureIdentifier;
                }

                model.bind();
                model.draw();
            }
        }

        shaderProgram.unbind();
    }

    private Entity getCamera() {
        var manager = ClientBase.getInstance().getManager();
        return manager.getFirstEntityWith(CameraComponent.class, TransformationComponent.class);
    }
}
