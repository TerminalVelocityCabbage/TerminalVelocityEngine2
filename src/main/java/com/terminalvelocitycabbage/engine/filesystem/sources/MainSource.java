package com.terminalvelocitycabbage.engine.filesystem.sources;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.ResourceRoot;
import com.terminalvelocitycabbage.engine.filesystem.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.ResourceType;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.URLResource;
import com.terminalvelocitycabbage.engine.registry.Identifier;

/**
 * A source which finds its resources within the main program, like the game, or internal dependencies
 */
public class MainSource extends ResourceSource {

    Entrypoint entrypoint;

    public MainSource(String namespace, Entrypoint entrypoint) {
        super(namespace);
        this.entrypoint = entrypoint;
    }

    @Override
    public void registerDefaultSourceRoot(ResourceType type) {
        getResourceRootRegistry().register(new Identifier(namespace, type.getName()), new ResourceRoot(type, "assets/" + namespace + "/" + type.getName()));
    }

    @Override
    public Resource getResource(String name, ResourceType resourceType) {

        var root = new Identifier(namespace, resourceType.getName());
        Log.warn("rootName: " + root);

        String path = getResourceRootRegistry().get(root).path();
        Log.info("getResourcePath: " + path);

        return new URLResource(entrypoint.getClass().getClassLoader().getResource(path));
    }

}
