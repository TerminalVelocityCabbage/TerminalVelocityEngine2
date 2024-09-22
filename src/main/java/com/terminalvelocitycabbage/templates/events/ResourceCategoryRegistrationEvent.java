package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.event.RegistryEvent;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class ResourceCategoryRegistrationEvent extends RegistryEvent<ResourceCategory> {

    public static final Identifier EVENT = TerminalVelocityEngine.identifierOf("ResourceCategoryRegistrationEvent");

    public ResourceCategoryRegistrationEvent(Identifier name, Registry<ResourceCategory> registry) {
        super(name, registry);
    }

}
