package com.terminalvelocitycabbage.engine.filesystem.sources;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.ResourceType;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.URLResource;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Arrays;

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
        getResourceRootRegistry().register(new Identifier(namespace, type.getName()), "assets/" + namespace + "/" + type.getName() + "s");
        Log.warn("Registered source root: " + namespace + ":" + type.getName() + "s");
    }

    @Override
    public Resource getResource(String name, ResourceType resourceType) {

        var root = namespace + ":" + resourceType.getName();
        Log.warn("     found rootName: " + root);

        String path = getResourceRootRegistry().get(root);
        path = path.replace("\\", "/");
        Log.info("     found getResourcePath: " + path);

        String compiledPath = path + "/" + name;
        compiledPath = compiledPath.replace("\\", "/");
        Log.info("     looking in \"" + compiledPath + "\"");

        var filePath = entrypoint.getClass().getClassLoader().getResource(path);
        try {
            var file = Paths.get(filePath.toURI()).toFile();
            var files = file.listFiles();
            Log.info("something found? " + Arrays.stream(files).toList());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        return new URLResource(entrypoint.getClass().getClassLoader().getResource(compiledPath));
    }

}
