package com.terminalvelocitycabbage.engine.filesystem;

import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.registry.Registry;

import java.util.Collection;

/**
 * Defines a resource root for an entrypoint or asset pack
 */
public abstract class ResourceSource {

    protected String namespace;
    //Paths for the roots are strings
    Registry<String> resourceRootRegistry;

    public ResourceSource(String namespace) {
        this.namespace = namespace;
        this.resourceRootRegistry = new Registry<>(null);
    }

    public Registry<String> getResourceRootRegistry() {
        return resourceRootRegistry;
    }

    public abstract void registerDefaultSourceRoot(ResourceType type);

    public Collection<String> getRoots() {
        return resourceRootRegistry.getRegistryContents().values();
    }

    public abstract Resource getResource(String path, ResourceType resourceType);
}
