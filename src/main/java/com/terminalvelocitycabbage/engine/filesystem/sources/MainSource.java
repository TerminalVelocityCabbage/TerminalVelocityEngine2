package com.terminalvelocitycabbage.engine.filesystem.sources;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.resources.types.URLResource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A source which finds its resources within the main program, like the game, or internal dependencies
 */
public class MainSource extends ResourceSource {

    //The entrypoint of this source (usually extends ClientBase or ServerBase)
    Entrypoint entrypoint;

    public MainSource(Entrypoint entrypoint) {
        this(entrypoint, entrypoint.getNamespace());
    }

    public MainSource(Entrypoint entrypoint, String namespace) {
        super(namespace);
        this.entrypoint = entrypoint;
    }

    /**
     * @param name         The path to the resource being gotten
     * @param resourceCategory The type of resource being retrieved
     * @return             A resource from this game client/server
     */
    @Override
    public Resource getResource(String name, ResourceCategory resourceCategory) {

        String compiledPath = resourceCategory.getAssetsPath(namespace) + "/" + name;

        var resource = entrypoint.getClass().getClassLoader().getResource(compiledPath);
        if (resource == null) {
            Log.crash("Could not find file at requested path: " + compiledPath);
        }

        return new URLResource(resource);
    }

    @Override
    public Collection<String> enumerateResources(ResourceCategory category) {
        String assetsPath = category.getAssetsPath(namespace);
        URL url = entrypoint.getClass().getClassLoader().getResource(assetsPath);
        if (url == null) return Collections.emptyList();

        List<String> resources = new ArrayList<>();
        try {
            if (url.getProtocol().equals("file")) {
                File dir = new File(url.toURI());
                if (dir.isDirectory()) {
                    File[] files = dir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.isFile()) {
                                resources.add(file.getName());
                            }
                        }
                    }
                }
            } else if (url.getProtocol().equals("jar")) {
                String jarPath = url.getPath().substring(5, url.getPath().indexOf("!"));
                try (JarFile jarFile = new JarFile(URLDecoder.decode(jarPath, StandardCharsets.UTF_8))) {
                    Enumeration<JarEntry> entries = jarFile.entries();
                    String path = assetsPath + "/";
                    while (entries.hasMoreElements()) {
                        JarEntry entry = entries.nextElement();
                        String name = entry.getName();
                        if (name.startsWith(path) && !entry.isDirectory()) {
                            String fileName = name.substring(path.length());
                            if (!fileName.contains("/")) {
                                resources.add(fileName);
                            }
                        }
                    }
                }
            }
        } catch (URISyntaxException | IOException e) {
            Log.error("Could not enumerate resources in MainSource: " + e.getMessage());
        }
        return resources;
    }

}
