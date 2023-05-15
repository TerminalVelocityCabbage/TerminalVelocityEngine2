package com.terminalvelocitycabbage.engine.filesystem.sources;

import com.terminalvelocitycabbage.engine.filesystem.ResourceRoot;
import com.terminalvelocitycabbage.engine.filesystem.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.ResourceType;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.registry.Identifier;

/**
 * A source which finds its resources on the local file system like unpacked configs
 */
public class SystemSource extends ResourceSource {

    public SystemSource(String namespace) {
        super(namespace);
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
