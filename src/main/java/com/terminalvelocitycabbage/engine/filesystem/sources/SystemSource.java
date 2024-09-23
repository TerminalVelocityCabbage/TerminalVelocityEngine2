package com.terminalvelocitycabbage.engine.filesystem.sources;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
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
    public void registerDefaultSourceRoot(ResourceCategory type) {
        getResourceRootRegistry().register(new Identifier(namespace, type.name()), "assets/" + namespace + "/" + type.name() + "s");
    }

    @Override
    public Resource getResource(String path, ResourceCategory resourceCategory) {
        Log.error("Not implemented yet");
        return null;
    }

}
