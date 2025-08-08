package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.model.Model;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class ModelConfigRegistrationEvent extends RegistryEvent<Model> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("ModelConfigRegistrationEvent");

    public ModelConfigRegistrationEvent(Registry<Model> registry) {
        super(EVENT, registry);
    }
}
