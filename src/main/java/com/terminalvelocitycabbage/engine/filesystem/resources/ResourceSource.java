package com.terminalvelocitycabbage.engine.filesystem.resources;

import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.registry.Registry;

/**
 * Defines a set of resource roots for an entrypoint or asset pack. This is the literal set of resource locations as
 * they exist as file in the game or mod etc. artifacts. From this they will be compiled and added to the virtual
 * {@link GameFileSystem} for the game and other entrypoints to retrieve.
 */
public abstract class ResourceSource {

    //The namespace for the entrypoint of this Resource Source
    protected String namespace;
    //Paths for the roots are strings
    Registry<String> resourceRootRegistry;

    public ResourceSource(String namespace) {
        this.namespace = namespace;
        this.resourceRootRegistry = new Registry<>(null);
    }

    /**
     * @return The location to register resource roots
     */
    public Registry<String> getResourceRootRegistry() {
        return resourceRootRegistry;
    }

    /**
     * Registers a source root for the inheriting source types so that the location of a resource does not matter
     * to the resource filesystem compiler
     * @param type The type of resource being retrieved
     */
    public abstract void registerDefaultSourceRoot(ResourceCategory type);

    /**
     * @param path The path to the resource being gotten
     * @param resourceCategory The type of resource being retrieved
     * @return A resource in this resource source to be loaded into the filesystem
     */
    public abstract Resource getResource(String path, ResourceCategory resourceCategory);

    /**
     * Registers all the default {@link ResourceCategory}s to this source root registry
     */
    public void registerDefaultSources() {
        registerDefaultSourceRoot(ResourceCategory.MODEL);
        registerDefaultSourceRoot(ResourceCategory.TEXTURE);
        registerDefaultSourceRoot(ResourceCategory.ANIMATION);
        registerDefaultSourceRoot(ResourceCategory.SHADER);
        registerDefaultSourceRoot(ResourceCategory.DEFAULT_CONFIG);
        registerDefaultSourceRoot(ResourceCategory.SOUND);
        registerDefaultSourceRoot(ResourceCategory.FONT);
        registerDefaultSourceRoot(ResourceCategory.GENERIC_FILE);
        registerDefaultSourceRoot(ResourceCategory.LOCALIZATION);
        registerDefaultSourceRoot(ResourceCategory.PROPERTIES);
    }
}
