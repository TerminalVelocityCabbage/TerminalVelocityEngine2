package com.terminalvelocitycabbage.engine.filesystem.resources;

//TODO replace with resource type registry in case a mod has a unique resource type not listed here
/**
 * Defines a type of resource. For now
 */
public enum ResourceType {

    MODEL("model"),
    TEXTURE("texture"),
    ANIMATION("animation"),
    SHADER("shader"),
    DEFAULT_CONFIG("default_config"),
    SOUND("sound"),
    FONT("font"),
    GENERIC_FILE("file"),
    LOCALIZATION("localization");

    String name;

    ResourceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
