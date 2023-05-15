package com.terminalvelocitycabbage.engine.filesystem;

public enum ResourceType {

    MODEL("model"),
    TEXTURE("texture"),
    ANIMATION("animation"),
    SHADER("shader"),
    DEFAULT_CONFIG("default_config"),
    SOUND("sound"),
    FONT("font"),
    GENERIC_FILE("file");

    String name;

    ResourceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
