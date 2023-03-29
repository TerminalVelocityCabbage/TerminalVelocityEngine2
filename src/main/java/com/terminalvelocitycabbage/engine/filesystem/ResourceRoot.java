package com.terminalvelocitycabbage.engine.filesystem;

/**
 * Defines the root of a resource type for a given {@link ResourceSource}
 */
public record ResourceRoot(ResourceType type, String path) {

}
