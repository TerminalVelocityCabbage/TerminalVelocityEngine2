package com.terminalvelocitycabbage.engine.mod;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.types.JarResource;
import com.terminalvelocitycabbage.engine.networking.Side;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.server.ServerBase;
import com.terminalvelocitycabbage.engine.util.ClassUtils;
import com.terminalvelocitycabbage.engine.util.Toggle;

import javax.management.ReflectionException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModManager {

    private Map<String, Mod> modMap;
    private List<Mod> orderedModList;

    public ModManager() {
        modMap = new HashMap<>();
        orderedModList = new ArrayList<>();
    }

    public void loadAndRegisterMods(Side side) {

        Path modsDir = Paths.get("mods");
        File modsRoot = new File(modsDir.toUri());

        //Create the mods folder if it doesn't exist
        if (!modsRoot.exists()) {
            modsRoot.mkdirs();
        }

        //The list of mods found in this mods folder (not ordered by dependency yet)
        Map<String, Mod> unsortedMods = new HashMap<>();

        for (File file : modsRoot.listFiles()) {

            //If this file is not a jar mod skip it
            if (!file.getName().endsWith(".jar")) continue;
            //Get a jarFile from this file
            JarFile jarFile;
            try {
                jarFile = new JarFile(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            //Get the information needed about this mod from it's mod-info.toml file
            Config config = TomlFormat.instance().createParser().parse(getModInfoToml(jarFile));
            ModInfo modInfo = new ObjectConverter().toObject(config, ModInfo::new);

            //The entrypoints for this mod
            Entrypoint entrypoint = getEntrypointFromFile(file, side);

            if (entrypoint == null) {
                Log.error("Could not find any annotated entrypoint class for " + file.getName());
                continue;
            }


            //Create a new Mod instance from this information
            Mod mod = new Mod(entrypoint, jarFile, modInfo);

            unsortedMods.put(mod.getModInfo().getNamespace(), mod);
        }

        // Loop through all mods and check for dependencies
        // If a dep is requested check that it has been found in the list of mod files; crash if not
        // If the dep is found check that it does not rely on the mod which depends on it; crash if yes
        // If the dep is found and there is no circ deps add it to a list of dependencies and increment it's priority by one
        // Loop through all optional dependencies and add them to the list with priority 1 also
        // Recursively loop through the list of above dependencies incrementing dependencies of dependencies until the root is found.

        //Search for missing dependencies
        Toggle allDependenciesFound = new Toggle(true);
        unsortedMods.values().forEach(mod -> {
            checkModForDependencies(mod, unsortedMods, allDependenciesFound);
        });
        if (!allDependenciesFound.getStatus()) Log.crash("Mismatch or Missing Dependencies error",
                new RuntimeException("Could not initialize all mods due to mod dependency errors. See above list of dependency errors."));

        //Register the mods to the mod registry
        unsortedMods.forEach((s, mod) -> {
            switch (side) {
                case CLIENT -> ClientBase.getInstance().getModRegistry().register(new Identifier(mod.getModInfo().getNamespace(), mod.getModInfo().getNamespace()), mod);
                case SERVER -> ServerBase.getInstance().getModRegistry().register(new Identifier(mod.getModInfo().getNamespace(), mod.getModInfo().getNamespace()), mod);
            }

            modMap.put(mod.getModInfo().getNamespace(), mod);
        });
    }

    private void checkModForDependencies(Mod mod, Map<String, Mod> foundMods, Toggle allDependenciesFound) {
        mod.getModInfo().getRequiredDependencies().forEach(dep -> {
            if (foundMods.containsKey(dep.getValue0())) {
                Mod foundDep = foundMods.get(dep.getValue0());
                if (!foundDep.getModInfo().getVersion().isHigherThanOrEquivalentTo(dep.getValue1())) {
                    allDependenciesFound.disable();
                    Log.error(mod.getModInfo().getNamespace() + ":" + mod.getModInfo().getVersion() + " requires dependency: " + dep.getValue0() + ":" + dep.getValue1() + " version provided: " + foundDep.getModInfo().getVersion());
                } else {
                    checkModForDependencies(foundDep, foundMods, allDependenciesFound);
                }
            } else {
                allDependenciesFound.disable();
                Log.error(mod.getModInfo().getNamespace() + ":" + mod.getModInfo().getVersion() + " requires dependency: " + dep.getValue0() + ":" + dep.getValue1() + " not found");
            }
        });
    }

    private String getModInfoToml(JarFile jarFile) {

        Iterator<JarEntry> iterator = jarFile.entries().asIterator();

        while (iterator.hasNext()) {
            JarEntry entry = iterator.next();
            if (entry.getName().endsWith("mod-info.toml")) return new JarResource(jarFile, entry).asString();
        }

        Log.crash("Could not find mod-info.toml for mod in mods directory",
                new RuntimeException("An invalid mod was included in this mods directory."));

        return null;
    }

    private Entrypoint getEntrypointFromFile(File file, Side side) {
        try {
            //Get classes from this mod
            var classes = ClassUtils.getClassesFromJarFile(file);

            //Loop through all classes to find the client or server entrypoint
            for (Class<?> clazz : classes) {

                //Found mod entrypoint
                boolean client = clazz.isAnnotationPresent(ModClientEntrypoint.class);
                boolean server = clazz.isAnnotationPresent(ModServerEntrypoint.class);

                //If the current side and annotation side doesn't match, don't register this class
                if (!client && side == Side.CLIENT || !server && side == Side.SERVER) continue;

                //If they do match, this is the current entrypoint
                return (Entrypoint) ClassUtils.createInstance(clazz);
            }
        } catch (ReflectionException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public ModInfo getModInfo(String namespace) {
        return modMap.get(namespace).getModInfo();
    }

    public Mod getMod(String namespace) {
        return modMap.get(namespace);
    }
}
