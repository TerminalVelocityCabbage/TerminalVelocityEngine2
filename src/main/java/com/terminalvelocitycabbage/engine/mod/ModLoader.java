package com.terminalvelocitycabbage.engine.mod;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.util.ClassUtils;

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
                        }
                    }
                }
            }
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            throw new RuntimeException(e);
        }
    }
}
