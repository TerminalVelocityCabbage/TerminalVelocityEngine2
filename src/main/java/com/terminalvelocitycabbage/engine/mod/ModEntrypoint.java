package com.terminalvelocitycabbage.engine.mod;

import com.terminalvelocitycabbage.engine.Entrypoint;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;

import java.util.Map;

public abstract class ModEntrypoint extends Entrypoint {

    private Mod mod;
    private Map<String, Mod> dependencies;

    public ModEntrypoint(String namespace) {
        super(namespace);
    }

    public abstract void registerEventListeners();

    public Mod getMod() {
        return mod;
    }

    public Map<String, Mod> getDependencies() {
        return dependencies;
    }

    protected EventDispatcher getEventDispatcher() {
        return mod.getEventDispatcher();
    }
}
