package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class ResourceSourceRegistrationEvent extends RegistryEvent<ResourceSource> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("ResourceSourceRegistrationEvent");
    Entrypoint entrypoint;

    public ResourceSourceRegistrationEvent(Identifier name, Registry<ResourceSource> registry, Entrypoint entrypoint) {
        super(name, registry);
        this.entrypoint = entrypoint;
    }

    public Entrypoint getEntrypoint() {
        return entrypoint;
    }
}
