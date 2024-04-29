package com.terminalvelocitycabbage.engine.mod;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.github.zafarkhaja.semver.Version;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.types.JarResource;
import com.terminalvelocitycabbage.engine.networking.Side;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.engine.util.ClassUtils;
import com.terminalvelocitycabbage.engine.util.Toggle;
import com.terminalvelocitycabbage.engine.util.touples.Pair;

import javax.management.ReflectionException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModLoader {

    //Arbitrary number, not sure how many mods players will have in practice, but since the sorting algorithm used
    //for mod priority is pretty much linear this technically caps the mod count to this number in a way.
    //TODO replace this with a more clever way to detect circular dependencies larger than direct circ deps.
    public static final int MAX_SORT_ITERATIONS = 10000;

    public static void loadAndRegisterMods(Side side, Registry<Mod> modRegistry) {

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
            ModEntrypoint entrypoint = getEntrypointFromFile(file, side);

            if (entrypoint == null) {
                Log.error("Could not find any annotated entrypoint class for " + file.getName());
                continue;
            }


            //Create a new Mod instance from this information
            Mod mod = new Mod(entrypoint, jarFile, modInfo);

            //Set the private fields of this mod's entrypoint to this mod with reflection
            setModEntrypointMod(mod.getEntrypoint(), mod);

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
        if (!allDependenciesFound.getStatus()) {
            Log.crash("Mismatch or Missing Dependencies error",
                    new RuntimeException("Could not initialize all mods due to mod dependency errors. See above list of dependency errors."));
        }

        //Sort mods
        List<Mod> sortedMods = sortModsByDependency(unsortedMods);

        //Register the mods to the mod registry
        sortedMods.forEach(mod -> {
            modRegistry.register(new Identifier(mod.getModInfo().getNamespace(), mod.getModInfo().getNamespace()), mod);
        });

        //Set all mods dependencies field with reflection
        sortedMods.forEach(mod -> {
            setModDependencies(modRegistry, mod, mod.getEntrypoint(), unsortedMods);
        });
    }

    private static void setModDependencies(Registry<Mod> modRegistry, Mod mod, ModEntrypoint entrypoint, Map<String, Mod> mods) {
        Map<String, Mod> dependencies = new HashMap<>();
        modRegistry.get(new Identifier(mod.getModInfo().getNamespace(), mod.getModInfo().getNamespace())).getModInfo().getAllDependencies().forEach(modDependency -> {
            if (mods.containsKey(modDependency.getValue0())) dependencies.put(modDependency.getValue0(), mods.get(modDependency.getValue0()));
        });
        try {
            Field modField = entrypoint.getClass().getSuperclass().getDeclaredField("dependencies");
            modField.setAccessible(true);
            modField.set(entrypoint, dependencies);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static void setModEntrypointMod(ModEntrypoint entrypoint, Mod mod) {
        try {
            Field modField = entrypoint.getClass().getSuperclass().getDeclaredField("mod");
            modField.setAccessible(true);
            modField.set(entrypoint, mod);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sort mods until they are in dependency order. This sort works by iterating through a copy of the unsorted list
     * and moving dependency mods to the front until all mods that other mods depend on are located before the mods
     * that depend on them.
     * @param unsortedMods The list of mods to sort through
     * @return A linked list of
     */
    private static List<Mod> sortModsByDependency(Map<String, Mod> unsortedMods) {

        LinkedList<Mod> sortedMods = new LinkedList<>(unsortedMods.values());
        List<Mod> modsToMoveToFrontOfList = new ArrayList<>();

        int numSortingIterations = 0;

        while (true) {
            if (numSortingIterations >= MAX_SORT_ITERATIONS) {
                Log.crash("Crash whilst sorting mods by dependencies",
                        new RuntimeException("Could not complete sorting mods by dependencies. This usually happens when " +
                                "there is an undetected circular dependency in your mods. For example if: " +
                                "Mod A depends on Mod B depends on Mod C depends on Mod A again. TVE does its best to " +
                                "detect circular dependencies but can only detect direct circ deps like A depends on " +
                                "B depends on A."));
            }
            modsToMoveToFrontOfList.clear();
            for (int i = 0; i < unsortedMods.size(); i++) {
                //Det all of the dependencies of this mod
                for (Pair<String, Version> stringVersionPair : sortedMods.get(i).getModInfo().getAllDependencies()) {
                    //Since some dependencies are optional we can skip it if it does not exist on the registry since
                    //Required dependencies have been validated by now
                    if (unsortedMods.containsKey(stringVersionPair.getValue0())) {
                        Mod dependency = unsortedMods.get(stringVersionPair.getValue0());
                        //If the index of the dependency is greater than the index of this mod in the sorted list
                        //That means that it would be registered too late, move it to the front of the list.
                        if (sortedMods.indexOf(dependency) > i) {
                            modsToMoveToFrontOfList.add(dependency);
                        }
                    }
                }
            }
            //If the list of mods to be moved is empty that means that we did not find any mods that are out of order
            if (modsToMoveToFrontOfList.isEmpty()) return sortedMods;
            //If we did find some mods that are too late in the list, move them to the front
            sortedMods.removeAll(modsToMoveToFrontOfList);
            modsToMoveToFrontOfList.forEach(sortedMods::addFirst);
            //Increment the number of sorting iterations in case we get to a larger than detectable circular dependency
            numSortingIterations++;
        }
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
    private static void checkModForDependencies(Mod mod, Map<String, Mod> foundMods, Toggle allDependenciesFound) {
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
                    foundDep.getModInfo().getAllDependencies().forEach(stringVersionPair -> {
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

    private static String getModInfoToml(JarFile jarFile) {

        Iterator<JarEntry> iterator = jarFile.entries().asIterator();

        while (iterator.hasNext()) {
            JarEntry entry = iterator.next();
            if (entry.getName().endsWith("mod-info.toml")) return new JarResource(jarFile, entry).asString();
        }

        Log.crash("Could not find mod-info.toml for mod in mods directory",
                new RuntimeException("An invalid mod was included in this mods directory."));

        return null;
    }

    private static ModEntrypoint getEntrypointFromFile(File file, Side side) {
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
                return (ModEntrypoint) ClassUtils.createInstance(clazz);
            }
        } catch (ReflectionException | IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return null;
    }
}
