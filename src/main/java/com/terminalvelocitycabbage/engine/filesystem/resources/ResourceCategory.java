package com.terminalvelocitycabbage.engine.filesystem.resources;

import com.terminalvelocitycabbage.engine.registry.Identifiable;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

/**
 * @param namespace the namespace of this resource category
 * @param name the name of this resource category, and the path appended by an "s" to the default resource root
 */
public record ResourceCategory(String namespace, String name) implements Identifiable {

    public static ResourceCategory MODEL;
    public static ResourceCategory TEXTURE;
    public static ResourceCategory ANIMATION;
    public static ResourceCategory SHADER;
    public static ResourceCategory DEFAULT_CONFIG;
    public static ResourceCategory SOUND;
    public static ResourceCategory FONT;
    public static ResourceCategory GENERIC_FILE;
    public static ResourceCategory LOCALIZATION;
    public static ResourceCategory PROPERTIES;

    /**
     * @param registry the registry to register the default values to
     * @param namespace The namespace to register these under
     */
    public static void registerEngineDefaults(Registry<ResourceCategory> registry, String namespace) {
        MODEL = registry.getAndRegister(new ResourceCategory(namespace, "model")).getElement();
        TEXTURE = registry.getAndRegister(new ResourceCategory(namespace, "texture")).getElement();
        ANIMATION = registry.getAndRegister(new ResourceCategory(namespace, "animation")).getElement();
        SHADER = registry.getAndRegister(new ResourceCategory(namespace, "shader")).getElement();
        DEFAULT_CONFIG = registry.getAndRegister(new ResourceCategory(namespace, "default_config")).getElement();
        SOUND = registry.getAndRegister(new ResourceCategory(namespace, "sound")).getElement();
        FONT = registry.getAndRegister(new ResourceCategory(namespace, "font")).getElement();
        GENERIC_FILE = registry.getAndRegister(new ResourceCategory(namespace, "generic_file")).getElement();
        LOCALIZATION = registry.getAndRegister(new ResourceCategory(namespace, "localization")).getElement();
        PROPERTIES = registry.getAndRegister(new ResourceCategory(namespace, "properties")).getElement();
    }

    @Override
    public Identifier getIdentifier() {
        return new Identifier(namespace, "resource_category", name);
    }

    public String getAssetsPath() {
        return "assets/" + namespace + "/" + name + "s";
    }
}
