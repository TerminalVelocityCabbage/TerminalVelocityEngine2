package com.terminalvelocitycabbage.engine.mod;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.debug.Log;

import java.util.Objects;
import java.util.jar.JarFile;

public class Mod {

    Entrypoint entrypoint;
    JarFile jarFile;
    ModInfo modInfo;

    /**
     * @param entrypoint The entrypoint can be a client entrypoint or server entrypoint, the mod registry does not care
     *                   since only the side that the mod is registered from will be initialized by the mod manager
     * @param jarFile The jar file that this mod's sources and resources belong to
     * @param info The mod-info.toml file that defines the metadata and dependencies for this mod.
     */
    public Mod(Entrypoint entrypoint, JarFile jarFile, ModInfo info) {
        if (!Objects.equals(entrypoint.getNamespace(), info.getNamespace())) {
            Log.crash("Mod Constructor namespace (" + entrypoint.getNamespace() + ") does not match mod-info.toml (" + info.getNamespace() + ")",
                    new RuntimeException("Could not instantiate mod"));
        }
        this.entrypoint = entrypoint;
        this.jarFile = jarFile;
        this.modInfo = info;
    }

    public Entrypoint getEntrypoint() {
        return entrypoint;
    }

    public JarFile getJarFile() {
        return jarFile;
    }

    public ModInfo getModInfo() {
        return modInfo;
    }
}
