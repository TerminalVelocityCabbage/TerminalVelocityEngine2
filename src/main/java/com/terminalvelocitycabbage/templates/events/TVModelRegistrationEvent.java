package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.model.formats.TVModel;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class TVModelRegistrationEvent extends RegistryEvent<TVModel> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "tv_model_registration");

    public TVModelRegistrationEvent(Registry<TVModel> registry) {
        super(EVENT, registry);
    }

    public Identifier registerTVModel(String namespace, Identifier modelResource) {
        TVModel model = TVModel.of(modelResource);
        return register(new Identifier(namespace, "tv_model", model.metadata().name()), model).getIdentifier();
    }
}
