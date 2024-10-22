package com.terminalvelocitycabbage.engine.filesystem.resources;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

/**
 * @param name the name of this resource category, and the path appended by an "s" to the default resource root
 */
public record ResourceCategory(String name) {

    public static final ResourceCategory MODEL = new ResourceCategory("model");
    public static final ResourceCategory TEXTURE = new ResourceCategory("texture");
    public static final ResourceCategory ANIMATION = new ResourceCategory("animation");
    public static final ResourceCategory SHADER = new ResourceCategory("shader");
    public static final ResourceCategory DEFAULT_CONFIG = new ResourceCategory("default_config");
    public static final ResourceCategory SOUND = new ResourceCategory("sound");
    public static final ResourceCategory FONT = new ResourceCategory("font");
    public static final ResourceCategory GENERIC_FILE = new ResourceCategory("file");
    public static final ResourceCategory LOCALIZATION = new ResourceCategory("localization");
    public static final ResourceCategory PROPERTIES = new ResourceCategory("propertie");

    /**
     * @param registry the registry to register the default values to
     * @param namespace The namespace to register these under
     */
    public static void registerEngineDefaults(Registry<ResourceCategory> registry, String namespace) {
        registry.register(new Identifier(namespace, "model_category"), MODEL);
        registry.register(new Identifier(namespace, "texture_category"), TEXTURE);
        registry.register(new Identifier(namespace, "animation_category"), ANIMATION);
        registry.register(new Identifier(namespace, "shader_category"), SHADER);
        registry.register(new Identifier(namespace, "default_config_category"), DEFAULT_CONFIG);
        registry.register(new Identifier(namespace, "sound_category"), SOUND);
        registry.register(new Identifier(namespace, "font_category"), FONT);
        registry.register(new Identifier(namespace, "generic_file_category"), GENERIC_FILE);
        registry.register(new Identifier(namespace, "localization_category"), LOCALIZATION);
        registry.register(new Identifier(namespace, "properties_category"), PROPERTIES);
    }

}
