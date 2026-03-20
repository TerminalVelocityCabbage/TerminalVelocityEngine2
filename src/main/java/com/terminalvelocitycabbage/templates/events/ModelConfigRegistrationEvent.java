package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.model.MeshTexturePair;
import com.terminalvelocitycabbage.engine.client.renderer.model.ModelConfig;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

import java.util.List;

public class ModelConfigRegistrationEvent extends RegistryEvent<ModelConfig> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "model_config_registration");

    public ModelConfigRegistrationEvent(Registry<ModelConfig> registry) {
        super(EVENT, registry);
    }

    public Identifier registerModel(String namespace, String modelName, List<MeshTexturePair> meshTexturePairs) {
        return register(new Identifier(namespace, "model", modelName), new ModelConfig(meshTexturePairs)).getIdentifier();
    }
}
