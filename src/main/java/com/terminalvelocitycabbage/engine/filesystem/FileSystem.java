package com.terminalvelocitycabbage.engine.filesystem;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;

public class FileSystem {

    Registry<ResourceSource> sourceRegistry;

    public FileSystem() {
        this.sourceRegistry = new Registry<>(null);
    }

    public Registry<ResourceSource> getSourceRegistry() {
        return sourceRegistry;
    }

    /**
     * Registers the provided source to this sourceRegistry with an identifier of "namespace:resource_source"
     *
     * @param namespace the namespace of this source
     * @param source the actual source to register
     */
    public void registerResourceSource(String namespace, ResourceSource source) {
        getSourceRegistry().register(new Identifier(namespace, "resource_source"), source);
    }

    public void init() {
        Log.info("initializing filesystem with " + getSourceRegistry().getRegistryContents().size() + " sources.");

        getSourceRegistry().getRegistryContents().forEach((identifier, source) ->  {
            source.getResourceRootRegistry().getRegistryContents().forEach((identifier1, resourceRoot) -> {
                Log.info("resource root found: " + resourceRoot.path());
            });
        });
    }

}
