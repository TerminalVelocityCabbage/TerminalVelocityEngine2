package com.terminalvelocitycabbage.engine.mod;

import com.github.simplenet.Server;
import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.server.ServerBase;
import com.terminalvelocitycabbage.engine.util.ClassUtils;

import javax.management.ReflectionException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ModLoader {

    public static void getModEntrypoints() {

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

                    for (Class<?> clazz : classes) {

                        //Found mod entrypoint
                        if (clazz.isAnnotationPresent(Mod.class)) {
                            Log.info(clazz.getName());
                            var mod = (Entrypoint) ClassUtils.createInstance(clazz);
                            ServerBase.getInstance().getModRegistry().register(new Identifier(mod.getNamespace(), mod.getNamespace()), mod);
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | NullPointerException | ReflectionException e) {
            throw new RuntimeException(e);
        }
    }
}
