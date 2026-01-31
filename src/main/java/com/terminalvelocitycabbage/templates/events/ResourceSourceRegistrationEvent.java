package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class ResourceSourceRegistrationEvent extends RegistryEvent<ResourceSource> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("event", "resource_source_registration");
    Entrypoint entrypoint;

    public ResourceSourceRegistrationEvent(Registry<ResourceSource> registry, Entrypoint entrypoint) {
        super(EVENT, registry);
        this.entrypoint = entrypoint;
    }

    public Identifier registerResourceSource(String namespace, String name, ResourceSource source) {
        return register(new Identifier(namespace, "resource_source", name), source).getIdentifier();
    }

    public Entrypoint getEntrypoint() {
        return entrypoint;
    }
}
