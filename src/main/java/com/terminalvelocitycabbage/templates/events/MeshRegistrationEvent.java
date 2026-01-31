package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.client.renderer.model.Mesh;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class MeshRegistrationEvent extends RegistryEvent<Mesh> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "mesh_registration");

    public MeshRegistrationEvent(Registry<Mesh> registry) {
        super(EVENT, registry);
    }

    public Identifier registerMesh(String namespace, String name, Mesh mesh) {
        return register(new Identifier(namespace, "mesh", name), mesh).getIdentifier();
    }
}
