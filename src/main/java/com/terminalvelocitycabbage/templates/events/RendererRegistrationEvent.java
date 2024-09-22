package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class RendererRegistrationEvent extends RegistryEvent<RenderGraph> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("RendererRegistrationEvent");

    public RendererRegistrationEvent(Registry<RenderGraph> registry) {
        super(EVENT, registry);
    }
}
