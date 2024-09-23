package com.terminalvelocitycabbage.engine.filesystem.resources;

import com.terminalvelocitycabbage.engine.registry.Identifier;

/**
 * @param resourceSourceIdentifier The source of this resource, for example "game:client_main_resource_source"
 *                                 see {@link ResourceSource}
 * @param type The type of resource this is {@link ResourceCategory}
 * @param resourceIdentifier The identifier for this resource, example "game:trex"
 */
public record ResourceLocation(
        Identifier resourceSourceIdentifier,
        ResourceCategory type,
        Identifier resourceIdentifier
) {

}
