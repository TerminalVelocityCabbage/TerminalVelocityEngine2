package com.terminalvelocitycabbage.templates.events;

import com.terminalvelocitycabbage.engine.TerminalVelocityEngine;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.event.Event;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceLocation;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.RegistryPair;

public class ResourceRegistrationEvent extends Event {

    private final GameFileSystem gameFileSystem;

    public ResourceRegistrationEvent(GameFileSystem gameFileSystem, ResourceCategory category) {
        super(getEventNameFromCategory(category));
        this.gameFileSystem = gameFileSystem;
    }

    public static Identifier getEventNameFromCategory(ResourceCategory category) {
        return TerminalVelocityEngine.identifierOf("ResourceRegistrationEvent[" + category.name() + "]");
    }

    public RegistryPair<ResourceLocation> registerResource(Identifier resourceSourceIdentifier, ResourceCategory category, String fileName) {
        return gameFileSystem.registerResource(resourceSourceIdentifier, category, fileName);
    }
}
