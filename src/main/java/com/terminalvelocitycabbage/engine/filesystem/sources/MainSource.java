package com.terminalvelocitycabbage.engine.filesystem.sources;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceType;
import com.terminalvelocitycabbage.engine.filesystem.resources.types.URLResource;
import com.terminalvelocitycabbage.engine.registry.Identifier;

/**
 * A source which finds its resources within the main program, like the game, or internal dependencies
 */
public class MainSource extends ResourceSource {

    //The entrypoint of this source (usually extends ClientBase or ServerBase)
    Entrypoint entrypoint;

    public MainSource(String namespace, Entrypoint entrypoint) {
        super(namespace);
        this.entrypoint = entrypoint;
    }

    /**
     * Registers a resource root within this game artifact
     * @param type The type of resource being retrieved
     */
    @Override
    public void registerDefaultSourceRoot(ResourceType type) {
        getResourceRootRegistry().register(new Identifier(namespace, type.getName()), "assets/" + namespace + "/" + type.getName() + "s");
    }

    /**
     * @param name         The path to the resource being gotten
     * @param resourceType The type of resource being retrieved
     * @return             A resource from this game client/server
     */
    @Override
    public Resource getResource(String name, ResourceType resourceType) {

        var root = namespace + ":" + resourceType.getName();
        String path = getResourceRootRegistry().get(root);
        String compiledPath = path + "/" + name;

        var resource = entrypoint.getClass().getClassLoader().getResource(compiledPath);
        if (resource == null) {
            Log.crash("Could not find file at requested path: " + compiledPath);
        }

        return new URLResource(resource);
    }

}
