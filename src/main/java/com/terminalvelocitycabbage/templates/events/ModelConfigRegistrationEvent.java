package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.model.Model;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class ModelConfigRegistrationEvent extends RegistryEvent<Model> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "model_config_registration");

    public ModelConfigRegistrationEvent(Registry<Model> registry) {
        super(EVENT, registry);
    }

    public Identifier registerModel(String namespace, String modelName, Identifier mesh, Identifier texture) {
        return register(new Identifier(namespace, "model", modelName), new Model(mesh, texture)).getIdentifier();
    }
}
