package com.terminalvelocitycabbage.engine.filesystem.sources;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;

import java.util.Collection;
import java.util.Collections;

/**
 * A source which finds its resources on the local file system like unpacked configs
 */
public class SystemSource extends ResourceSource {

    public SystemSource(String namespace) {
        super(namespace);
    }

    @Override
    public Resource getResource(String path, ResourceCategory resourceCategory) {
        Log.error("Not implemented yet");
        return null;
    }

    @Override
    public Collection<String> enumerateResources(ResourceCategory category) {
        Log.error("Not implemented yet");
        return Collections.emptyList();
    }

}
