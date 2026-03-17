package com.terminalvelocitycabbage.engine.filesystem.sources;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.resources.types.JarResource;
import com.terminalvelocitycabbage.engine.mod.Mod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A Source which finds its resources within a jar NOT associated with the main program like mods or external deps
 */
public class ModSource extends ResourceSource {

    //The mod which hosts this source
    Mod mod;

    public ModSource(Mod mod) {
        super(mod.getEntrypoint().getNamespace());
        this.mod = mod;
    }

    /**
     * @param name         The path to the resource being gotten
     * @param resourceCategory The type of resource being retrieved
     * @return             A resource from this mod's jar
     */
    @Override
    public Resource getResource(String name, ResourceCategory resourceCategory) {

        String compiledPath = resourceCategory.getAssetsPath(mod.getEntrypoint().getNamespace()) + "/" + name;
        JarFile jarFile = mod.getJarFile();
        JarEntry entry = jarFile.getJarEntry(compiledPath);

        if (entry == null) {
            Log.crash("Could not find jarfile entry at requested path: " + compiledPath);
        }

        return new JarResource(jarFile, entry);
    }

    @Override
    public Collection<String> enumerateResources(ResourceCategory category) {
        String path = category.getAssetsPath(namespace) + "/";
        List<String> resources = new ArrayList<>();
        Enumeration<JarEntry> entries = mod.getJarFile().entries();
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
        return resources;
    }

}
