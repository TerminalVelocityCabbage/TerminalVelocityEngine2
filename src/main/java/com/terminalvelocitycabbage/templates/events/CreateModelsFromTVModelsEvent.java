package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.Mesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.ModelConfig;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVModel;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVModelDataMesh;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class CreateModelsFromTVModelsEvent extends Event {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "create_models_from_tv_models");

    private final Registry<TVModel> tvModelRegistry;
    private final Registry<Mesh> meshRegistry;
    private final Registry<ModelConfig> modelConfigRegistry;

    public CreateModelsFromTVModelsEvent(Registry<TVModel> tvModelRegistry, Registry<Mesh> meshRegistry, Registry<ModelConfig> modelConfigRegistry) {
        super(EVENT);
        this.tvModelRegistry = tvModelRegistry;
        this.meshRegistry = meshRegistry;
        this.modelConfigRegistry = modelConfigRegistry;
    }

    public void createAllModels(String namespace, VertexFormat meshFormat) {
        tvModelRegistry.getRegistryContents().values().forEach(model -> createModel(namespace, model, meshFormat));
    }

    public void createModel(String namespace, TVModel model, VertexFormat meshFormat) {
        // Register meshes for each layer
        for (String layer : model.metadata().textureLayers().keySet()) {
            Identifier meshId = new Identifier(namespace, "mesh", model.metadata().name() + "_" + layer);
            meshRegistry.register(meshId, new Mesh(meshFormat, new TVModelDataMesh(model, layer)));
        }
        // Register model config for each variant
        for (String variantName : model.variants().keySet()) {
            Identifier modelId = new Identifier(namespace, "model_variant", model.metadata().name() + "_" + variantName);
            modelConfigRegistry.register(modelId, TVModel.configOf(namespace, model, variantName));
        }
    }
}
