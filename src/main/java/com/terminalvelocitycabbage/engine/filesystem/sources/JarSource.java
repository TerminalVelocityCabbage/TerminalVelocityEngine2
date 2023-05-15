package com.terminalvelocitycabbage.engine.filesystem.sources;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.filesystem.ResourceRoot;
import com.terminalvelocitycabbage.engine.filesystem.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.ResourceType;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.registry.Identifier;

/**
 * A Source which finds its resources within a jar NOT associated with the main program like mods or external deps
 */
public class JarSource extends ResourceSource {

    Entrypoint entrypoint;

    public JarSource(String namespace, Entrypoint entrypoint) {
        super(namespace);
        this.entrypoint = entrypoint;
    }

    @Override
    public void registerDefaultSourceRoot(ResourceType type) {
        getResourceRootRegistry().register(new Identifier(namespace, type.getName()), new ResourceRoot(type, "assets/" + namespace + "/" + type.getName()));
    }

    @Override
    public Resource getResource(String path, ResourceType resourceType) {
        return null;
    }

}
