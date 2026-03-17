package com.terminalvelocitycabbage.engine.filesystem.resources;

import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

/**
 * @param name the name of this resource category, and the path appended by an "s" to the default resource root
 * @param plural the plural name of this resource category used for the assets path
 */
public record ResourceCategory(String name, String plural) {

    public ResourceCategory(String name) {
        this(name, name + "s");
    }

    public static final ResourceCategory MODEL = new ResourceCategory("model");
    public static final ResourceCategory TEXTURE = new ResourceCategory("texture");
    public static final ResourceCategory ANIMATION = new ResourceCategory("animation");
    public static final ResourceCategory SHADER = new ResourceCategory("shader");
    public static final ResourceCategory DEFAULT_CONFIG = new ResourceCategory("default_config");
    public static final ResourceCategory SOUND = new ResourceCategory("sound");
    public static final ResourceCategory FONT = new ResourceCategory("font");
    public static final ResourceCategory GENERIC_FILE = new ResourceCategory("generic_file");
    public static final ResourceCategory LOCALIZATION = new ResourceCategory("localization");
    public static final ResourceCategory PROPERTIES = new ResourceCategory("property", "properties");
    public static final ResourceCategory ENTITY = new ResourceCategory("entity", "entities");
    public static final ResourceCategory ROUTINE = new ResourceCategory("routine");
    public static final ResourceCategory SCENE = new ResourceCategory("scene");

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
        register(registry, namespace, ENTITY);
        register(registry, namespace, ROUTINE);
        register(registry, namespace, SCENE);
    }

    private static void register(Registry<ResourceCategory> registry, String namespace, ResourceCategory resourceCategory) {
        registry.getAndRegister(resourceCategory.createIdentifier(namespace), resourceCategory);
    }

    public Identifier createIdentifier(String namespace) {
        return new Identifier(namespace, "resource_category", this.name());
    }

    public String getAssetsPath(String namespace) {
        return "assets/" + namespace + "/" + plural;
    }

    public Identifier identifierOf(String namespace, String name) {
        return new Identifier(namespace, this.name(), name);
    }
}
