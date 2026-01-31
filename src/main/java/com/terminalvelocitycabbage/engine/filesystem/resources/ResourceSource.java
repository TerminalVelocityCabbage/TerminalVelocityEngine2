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
     * @param namespace The namespace of the entrypoint that owns this resource source
     * @param type The type of resource being retrieved
     */
    public void registerDefaultSourceRoot(String namespace, ResourceCategory type) {
        getResourceRootRegistry().getAndRegister(type.createIdentifier(namespace), type.getAssetsPath(namespace));
    }

    /**
     * @param path The path to the resource being gotten
     * @param resourceCategory The type of resource being retrieved
     * @return A resource in this resource source to be loaded into the filesystem
     */
    public abstract Resource getResource(String path, ResourceCategory resourceCategory);

    /**
     * Registers all the default {@link ResourceCategory}s to this source root registry
     */
    public void registerDefaultSources(String namespace) {
        registerDefaultSourceRoot(namespace, ResourceCategory.MODEL);
        registerDefaultSourceRoot(namespace, ResourceCategory.TEXTURE);
        registerDefaultSourceRoot(namespace, ResourceCategory.ANIMATION);
        registerDefaultSourceRoot(namespace, ResourceCategory.SHADER);
        registerDefaultSourceRoot(namespace, ResourceCategory.DEFAULT_CONFIG);
        registerDefaultSourceRoot(namespace, ResourceCategory.SOUND);
        registerDefaultSourceRoot(namespace, ResourceCategory.FONT);
        registerDefaultSourceRoot(namespace, ResourceCategory.GENERIC_FILE);
        registerDefaultSourceRoot(namespace, ResourceCategory.LOCALIZATION);
        registerDefaultSourceRoot(namespace, ResourceCategory.PROPERTIES);
    }
}
