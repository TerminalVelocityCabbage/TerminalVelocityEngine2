package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class RendererRegistrationEvent extends RegistryEvent<RenderGraph> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "renderer_registration");

    public RendererRegistrationEvent(Registry<RenderGraph> registry) {
        super(EVENT, registry);
    }

    public Identifier registerNode(String namespace, String name) {
        return new Identifier(namespace, "render_node", name);
    }

    public Identifier registerGraph(String namespace, String name, RenderGraph graph) {
        return register(new Identifier(namespace, "render_graph", name), graph).getIdentifier();
    }

    public Identifier registerRoute(String namespace, String name) {
        return new Identifier(namespace, "render_route", name);
    }
}
