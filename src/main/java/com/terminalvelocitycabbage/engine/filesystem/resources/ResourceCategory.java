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
     */
    public static void registerEngineDefaults(Registry<ResourceCategory> registry) {
        register(registry, MODEL);
        register(registry, TEXTURE);
        register(registry, ANIMATION);
        register(registry, SHADER);
        register(registry, DEFAULT_CONFIG);
        register(registry, SOUND);
        register(registry, FONT);
        register(registry, GENERIC_FILE);
        register(registry, LOCALIZATION);
        register(registry, PROPERTIES);
        register(registry, ENTITY);
        register(registry, ROUTINE);
        register(registry, SCENE);
    }

    private static void register(Registry<ResourceCategory> registry, ResourceCategory resourceCategory) {
        registry.getAndRegister(resourceCategory.createIdentifier(), resourceCategory);
    }

    public Identifier createIdentifier() {
        return new Identifier("global", "resource_category", this.name());
    }

    public String getAssetsPath(String namespace) {
        return "assets/" + namespace + "/" + plural;
    }

    public Identifier identifierOf(String namespace, String name) {
        return new Identifier(namespace, this.name(), name);
    }
}
