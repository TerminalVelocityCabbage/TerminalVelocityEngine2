package com.terminalvelocitycabbage.engine.filesystem.sources;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.ResourceType;
import com.terminalvelocitycabbage.engine.filesystem.resources.JarResource;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.mod.Mod;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A Source which finds its resources within a jar NOT associated with the main program like mods or external deps
 */
public class JarSource extends ResourceSource {

    Mod mod;

    public JarSource(String namespace, Mod mod) {
        super(namespace);
        this.mod = mod;
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

        JarFile jarFile = mod.jarFile();
        JarEntry entry = jarFile.getJarEntry(compiledPath);
        Log.info("      Found file: " + entry.getName());
        return new JarResource(jarFile, entry);
    }

}
