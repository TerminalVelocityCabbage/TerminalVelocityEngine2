package com.terminalvelocitycabbage.engine.mod;

import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.conversion.ObjectConverter;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.types.JarResource;
import com.terminalvelocitycabbage.engine.networking.Side;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.server.ServerBase;
import com.terminalvelocitycabbage.engine.util.ClassUtils;

import javax.management.ReflectionException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ModManager {

    private Map<Class<? extends Entrypoint>, Mod> entrypointModMap;
    private List<Mod> orderedModList;

    public ModManager() {
        entrypointModMap = new HashMap<>();
        orderedModList = new ArrayList<>();
    }

    //TODO use a metadata file to get this information from instead of using an annotation
    public void loadAndRegisterMods(Side side) {

        Path modsDir = Paths.get("mods");
        File modsRoot = new File(modsDir.toUri());

        //Create the mods folder if it doesn't exist
        if (!modsRoot.exists()) {
            modsRoot.mkdirs();
        }

        try {
            for (File file : modsRoot.listFiles()) {

                //If this file is not a mod skip it
                if (!file.getName().endsWith(".jar")) continue;

                //Get classes from this mod
                var classes = ClassUtils.getClassesFromJarFile(file);

                for (Class clazz : classes) {

                    //Found mod entrypoint
                    boolean client = clazz.isAnnotationPresent(ModClientEntrypoint.class);
                    boolean server = clazz.isAnnotationPresent(ModServerEntrypoint.class);

                    //If the side and annotations don't match, don't register this class
                    if (!client && side == Side.CLIENT || !server && side == Side.SERVER) continue;

                    //Get the entrypoint of this Mod
                    Entrypoint entrypoint = (Entrypoint) ClassUtils.createInstance(clazz);;
                    JarFile jarFile = new JarFile(file);

                    //Create an instance of this mod from it's entrypoint and Jar file
                    Mod mod = new Mod(entrypoint, jarFile, null);
                    JarEntry jarEntry = new JarEntry("assets/testmod/mod-info.toml");
                    JarResource resource = new JarResource(jarFile, jarEntry);
                    ConfigFormat<CommentedConfig> tomlFormat = TomlFormat.instance();
                    ConfigParser<CommentedConfig> jsonParser = tomlFormat.createParser();
                    Config config = jsonParser.parse(resource.asString());
                    ObjectConverter converter = new ObjectConverter();
                    ModInfo modInfo = converter.toObject(config, ModInfo::new);
                    Log.info(modInfo.toString());

                    if (client && side == Side.CLIENT) {
                        ClientBase.getInstance().getModRegistry().register(new Identifier(entrypoint.getNamespace(), entrypoint.getNamespace()), mod);
                    }
                    if (server && side == Side.SERVER) {
                        ServerBase.getInstance().getModRegistry().register(new Identifier(entrypoint.getNamespace(), entrypoint.getNamespace()), mod);
                    }
                    entrypointModMap.put(entrypoint.getClass(), mod);
                }
            }
        } catch (IOException | ClassNotFoundException | NullPointerException | ReflectionException e) {
            throw new RuntimeException(e);
        } finally {
            //Loop through all mods and check for dependencies
            //If a dependency is requested check that it is registered, error if dependency is not registered
            //If a dependency is found and registered increment that dependency mod's "priority"
            //Do the same for optional dependencies but don't error if it's not registered
            //Loop through all dependency mods and check that there are no circular dependencies; error if so
            //Recursively increment dependency priorities until the highest priority is found
            //Set and sort a list of mod orders
        }
    }

    public ModInfo getModInfo(Class<? extends Entrypoint> clazz) {
        return entrypointModMap.get(clazz).info();
    }

    public Mod getMod(Entrypoint entrypoint) {
        return entrypointModMap.get(entrypoint.getClass());
    }
}
