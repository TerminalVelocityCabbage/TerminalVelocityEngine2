package com.terminalvelocitycabbage.engine.mod;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.networking.Side;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.server.ServerBase;
import com.terminalvelocitycabbage.engine.util.ClassUtils;

import javax.management.ReflectionException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarFile;

public class ModManager {

    private Map<Class<? extends Entrypoint>, Mod> entrypointModMap;

    public ModManager() {
        entrypointModMap = new HashMap<>();
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

                //If this file is a mod
                if (file.getName().endsWith(".jar")) {
                    var classes = ClassUtils.getClassesFromJarFile(file);

                    for (Class clazz : classes) {

                        //Found mod entrypoint
                        boolean client = clazz.isAnnotationPresent(ModClientEntrypoint.class);
                        boolean server = clazz.isAnnotationPresent(ModServerEntrypoint.class);

                        if (client || server) {
                            Entrypoint entrypoint;
                            JarFile jarFile = new JarFile(file);

                            if (client && side == Side.CLIENT) {
                                entrypoint = (Entrypoint) ClassUtils.createInstance(clazz);
                                Mod clientMod = new Mod(entrypoint, jarFile);
                                ClientBase.getInstance().getModRegistry().register(new Identifier(entrypoint.getNamespace(), entrypoint.getNamespace()), clientMod);
                                entrypointModMap.put(entrypoint.getClass(), clientMod);
                            }
                            if (server && side == Side.SERVER) {
                                entrypoint = (Entrypoint) ClassUtils.createInstance(clazz);
                                Mod serverMod = new Mod(entrypoint, jarFile);
                                ServerBase.getInstance().getModRegistry().register(new Identifier(entrypoint.getNamespace(), entrypoint.getNamespace()), serverMod);
                                entrypointModMap.put(entrypoint.getClass(), serverMod);
                            }
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | NullPointerException | ReflectionException e) {
            throw new RuntimeException(e);
        }
    }

    public Mod getMod(Entrypoint entrypoint) {
        return entrypointModMap.get(entrypoint.getClass());
    }
}
