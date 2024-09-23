package com.terminalvelocitycabbage.engine.mod;

import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;

import java.util.Objects;
import java.util.jar.JarFile;

public class Mod {

    ModEntrypoint entrypoint;
    JarFile jarFile;
    ModInfo modInfo;
    private final EventDispatcher eventDispatcher;

    /**
     * @param entrypoint      The entrypoint can be a client entrypoint or server entrypoint, the mod registry does not care
     *                        since only the side that the mod is registered from will be initialized by the mod manager
     * @param jarFile         The jar file that this mod's sources and resources belong to
     * @param info            The mod-info.toml file that defines the metadata and dependencies for this mod.
     * @param eventDispatcher The event dispatcher from the main entrypoint that this mod is loaded by
     */
    public Mod(ModEntrypoint entrypoint, JarFile jarFile, ModInfo info, EventDispatcher eventDispatcher) {
        if (!Objects.equals(entrypoint.getNamespace(), info.getNamespace())) {
            Log.crash("Mod Constructor namespace (" + entrypoint.getNamespace() + ") does not match mod-info.toml (" + info.getNamespace() + ")",
                    new RuntimeException("Could not instantiate mod"));
        }
        this.entrypoint = entrypoint;
        this.jarFile = jarFile;
        this.modInfo = info;
        this.eventDispatcher = eventDispatcher;
    }

    public ModEntrypoint getEntrypoint() {
        return entrypoint;
    }

    public JarFile getJarFile() {
        return jarFile;
    }

    public ModInfo getModInfo() {
        return modInfo;
    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }
}
