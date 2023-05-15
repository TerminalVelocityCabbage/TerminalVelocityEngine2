package com.terminalvelocitycabbage.engine.filesystem.resources;

import com.terminalvelocitycabbage.engine.filesystem.ResourceType;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public record ResourceLocation(
        Identifier resourceSourceIdentifier,
        ResourceType type,
        Identifier resourceIdentifier
) {

}
