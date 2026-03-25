package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.Mesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.MeshTexturePair;
import com.terminalvelocitycabbage.engine.client.renderer.model.ModelConfig;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVModel;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVModelDataMesh;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

import java.util.List;

public class ModelConfigRegistrationEvent extends RegistryEvent<ModelConfig> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "model_config_registration");

    private final Registry<TVModel> tvModelRegistry;
    private final Registry<Mesh> meshRegistry;

    public ModelConfigRegistrationEvent(Registry<ModelConfig> modelConfigRegistry, Registry<TVModel> tvModelRegistry, Registry<Mesh> meshRegistry) {
        super(EVENT, modelConfigRegistry);
        this.tvModelRegistry = tvModelRegistry;
        this.meshRegistry = meshRegistry;
    }

    public Identifier registerModel(String namespace, String modelName, Identifier meshIdentifier, Identifier textureIdentifier) {
        return registerModel(namespace, modelName, List.of(new MeshTexturePair(meshIdentifier, textureIdentifier)));
    }

    public Identifier registerModel(String namespace, String modelName, List<MeshTexturePair> meshTexturePairs) {
        return register(new Identifier(namespace, "model", modelName), new ModelConfig(meshTexturePairs)).getIdentifier();
    }

    public Identifier registerTVModel(String namespace, String modelName, VertexFormat meshFormat) {
        TVModel model = TVModel.of(ResourceCategory.MODEL.identifierOf(namespace, modelName));
        createModel(namespace, model, meshFormat);
        return tvModelRegistry.register(new Identifier(namespace, "tv_model", model.metadata().name()), model);
    }

    private void createModel(String namespace, TVModel model, VertexFormat meshFormat) {
        // Register meshes for each layer
        for (String layer : model.metadata().textureLayers().keySet()) {
            Identifier meshId = new Identifier(namespace, "mesh", model.metadata().name() + "_" + layer);
            meshRegistry.register(meshId, new Mesh(meshFormat, new TVModelDataMesh(model, layer)));
        }
        // Register model config for each variant
        for (String variantName : model.variants().keySet()) {
            Identifier modelId = new Identifier(namespace, "model_variant", model.metadata().name() + "_" + variantName);
            register(modelId, TVModel.configOf(namespace, model, variantName));
        }
    }

    public Identifier variantIdentifier(String namespace, String model, String variant) {
        return new Identifier(namespace, "model_variant", model + "_" + variant);
    }
}
