package com.terminalvelocitycabbage.engine.filesystem.sources;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.resources.types.URLResource;

/**
 * A source which finds its resources within the main program, like the game, or internal dependencies
 */
public class MainSource extends ResourceSource {

    //The entrypoint of this source (usually extends ClientBase or ServerBase)
    Entrypoint entrypoint;

    public MainSource(Entrypoint entrypoint) {
        super(entrypoint.getNamespace());
        this.entrypoint = entrypoint;
    }

    /**
     * @param name         The path to the resource being gotten
     * @param resourceCategory The type of resource being retrieved
     * @return             A resource from this game client/server
     */
    @Override
    public Resource getResource(String name, ResourceCategory resourceCategory) {

        String compiledPath = resourceCategory.getAssetsPath(entrypoint.getNamespace()) + "/" + name;

        var resource = entrypoint.getClass().getClassLoader().getResource(compiledPath);
        if (resource == null) {
            Log.crash("Could not find file at requested path: " + compiledPath);
        }

        return new URLResource(resource);
    }

}
