package com.terminalvelocitycabbage.engine.filesystem.resources;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

/**
 * @param name the name of this resource category, and the path appended by an "s" to the default resource root
 */
public record ResourceCategory(String name) {

    public static ResourceCategory MODEL = new ResourceCategory("model");
    public static ResourceCategory TEXTURE = new ResourceCategory("texture");
    public static ResourceCategory ANIMATION = new ResourceCategory("animation");
    public static ResourceCategory SHADER = new ResourceCategory("shader");
    public static ResourceCategory DEFAULT_CONFIG = new ResourceCategory("default_config");
    public static ResourceCategory SOUND = new ResourceCategory("sound");
    public static ResourceCategory FONT = new ResourceCategory("font");
    public static ResourceCategory GENERIC_FILE = new ResourceCategory("generic_file");
    public static ResourceCategory LOCALIZATION = new ResourceCategory("localization");
    public static ResourceCategory PROPERTIES = new ResourceCategory("properties");

    /**
     * @param registry the registry to register the default values to
     * @param namespace The namespace to register these under
     */
    public static void registerEngineDefaults(Registry<ResourceCategory> registry, String namespace) {
        register(registry, namespace, MODEL);
        register(registry, namespace, TEXTURE);
        register(registry, namespace, ANIMATION);
        register(registry, namespace, SHADER);
        register(registry, namespace, DEFAULT_CONFIG);
        register(registry, namespace, SOUND);
        register(registry, namespace, FONT);
        register(registry, namespace, GENERIC_FILE);
        register(registry, namespace, LOCALIZATION);
        register(registry, namespace, PROPERTIES);
    }

    private static void register(Registry<ResourceCategory> registry, String namespace, ResourceCategory resourceCategory) {
        registry.getAndRegister(resourceCategory.createIdentifier(namespace), resourceCategory);
    }

    public Identifier createIdentifier(String namespace) {
        return new Identifier(namespace, "resource_category", this.name());
    }

    public String getAssetsPath(String namespace) {
        return "assets/" + namespace + "/" + name + "s";
    }
}
