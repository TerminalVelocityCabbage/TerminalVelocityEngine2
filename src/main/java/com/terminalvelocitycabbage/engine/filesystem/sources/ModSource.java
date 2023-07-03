package com.terminalvelocitycabbage.engine.filesystem.sources;

import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceType;
import com.terminalvelocitycabbage.engine.filesystem.resources.types.JarResource;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.mod.Mod;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A Source which finds its resources within a jar NOT associated with the main program like mods or external deps
 */
public class ModSource extends ResourceSource {

    //The mod which hosts this source
    Mod mod;

    public ModSource(String namespace, Mod mod) {
        super(namespace);
        this.mod = mod;
    }

    /**
     * Registers a resource root within this mod's resources
     * @param type The type of resource being retrieved
     */
    @Override
    public void registerDefaultSourceRoot(ResourceType type) {
        getResourceRootRegistry().register(new Identifier(namespace, type.getName()), "assets/" + namespace + "/" + type.getName() + "s");
    }

    /**
     * @param name         The path to the resource being gotten
     * @param resourceType The type of resource being retrieved
     * @return             A resource from this mod's jar
     */
    @Override
    public Resource getResource(String name, ResourceType resourceType) {

        var root = namespace + ":" + resourceType.getName();
        String path = getResourceRootRegistry().get(root);
        String compiledPath = path + "/" + name;
        JarFile jarFile = mod.jarFile();
        JarEntry entry = jarFile.getJarEntry(compiledPath);

        return new JarResource(jarFile, entry);
    }

}
