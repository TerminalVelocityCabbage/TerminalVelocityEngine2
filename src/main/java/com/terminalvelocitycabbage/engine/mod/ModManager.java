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

        //Search for missing dependencies and crash if any are missing or outdated
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

    /**
     * This is a recursive function that checks all dependencies of this mod and it's dependencies
     * @param mod The mod whose dependencies we are checking
     * @param foundMods The list of mods that this ModManager found in the mods folder
     * @param allDependenciesFound A toggle (true/false) that we disable if a missing or mismatch dep is found, so we can
     *                             crash after we have looked up all missing dependencies, that way the user doesn't have
     *                             to download a dep, reload the game, just to find another dep is missing. This way
     *                             they get a list of all known missing deps before a crash. Obviously if a missing dep
     *                             has a dep that is not installed we can't know about that to crash, but this is close.
     */
    private void checkModForDependencies(Mod mod, Map<String, Mod> foundMods, Toggle allDependenciesFound) {
        //Find all <namespace:version> pars for requested dependencies from this mods dependency block
        mod.getModInfo().getRequiredDependencies().forEach(dep -> {
            //Check that the requested dependency's namespace exists on the found mods list
            if (foundMods.containsKey(dep.getValue0())) {
                //If the requested dep is found let's get the instance of that mod, so we can check the version matches too
                Mod foundDep = foundMods.get(dep.getValue0());
                //Check that the version provided is at least the version required
                if (!foundDep.getModInfo().getVersion().isHigherThanOrEquivalentTo(dep.getValue1())) {
                    //Let the user know that they have an outdated dependency
                    allDependenciesFound.disable();
                    Log.error(mod.getModInfo().getNamespace() + ":" + mod.getModInfo().getVersion() + " requires dependency: " + dep.getValue0() + ":" + dep.getValue1() + " outdated version provided: " + foundDep.getModInfo().getVersion());
                } else {
                    //Make sure that this dependency is not circular
                    foundDep.getModInfo().getRequiredDependencies().forEach(stringVersionPair -> {
                        if (stringVersionPair.getValue0().equals(mod.getModInfo().getNamespace())) {
                            //TODO determine if this should remain a hard crash or just be a warning to the users
                            allDependenciesFound.disable();
                            Log.error("Circular Dependency found: " + foundDep.getModInfo().getNamespace() + " and " + mod.getModInfo().getNamespace() + " depend on one another.",
                                    "This is not allowed for the sake of maintaining defined behavior. Dependencies are always registered first so that their code is executed first in the dependency tree.");
                        }
                    });
                    //If the requested dependency is found and is up-to-date and has no circular deps check its dependencies too.
                    checkModForDependencies(foundDep, foundMods, allDependenciesFound);
                }
            } else {
                //Let the user know that the dependency was not found at all in this mods folder
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
