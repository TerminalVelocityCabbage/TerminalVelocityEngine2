package com.terminalvelocitycabbage.engine.filesystem;

import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.registry.Registry;

import java.util.Collection;

/**
 * Defines a resource root for an entrypoint or asset pack
 */
public abstract class ResourceSource {

    protected String namespace;
    Registry<ResourceRoot> resourceRootRegistry;

    public ResourceSource(String namespace) {
        this.namespace = namespace;
        this.resourceRootRegistry = new Registry<>(null);
    }

    public Registry<ResourceRoot> getResourceRootRegistry() {
        return resourceRootRegistry;
    }

    public abstract void registerDefaultSourceRoot(ResourceType type);

    public Collection<ResourceRoot> getRoots() {
        return resourceRootRegistry.getRegistryContents().values();
    }

    public abstract Resource getResource(String path, ResourceType resourceType);
}
