package com.terminalvelocitycabbage.engine;

import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.filesystem.GameFileSystem;
import com.terminalvelocitycabbage.engine.graph.Routine;
import com.terminalvelocitycabbage.engine.mod.Mod;
import com.terminalvelocitycabbage.engine.networking.PacketRegistry;
import com.terminalvelocitycabbage.engine.registry.Registry;
import com.terminalvelocitycabbage.engine.scheduler.Scheduler;
import com.terminalvelocitycabbage.engine.translation.Localizer;
import com.terminalvelocitycabbage.engine.util.MutableInstant;
import com.terminalvelocitycabbage.engine.util.TickManager;

public abstract class MainEntrypoint extends Entrypoint {

    //Game loop stuff
    protected final Registry<Routine> routineRegistry;
    protected final TickManager tickManager;
    protected final Manager manager;
    protected final Scheduler scheduler;
    protected long deltaTime; //Tick delta time not render time
    protected final MutableInstant tickClock;

    //Networking stuff
    protected final PacketRegistry packetRegistry;

    //Scope Stuff
    protected final Registry<Mod> modRegistry;

    //Resources Stuff
    protected final GameFileSystem fileSystem;

    //Scene Stuff
    protected final Registry<Scene> sceneRegistry;

    //Localizations
    protected final Localizer localizer;


    protected final EventDispatcher eventDispatcher;

    public MainEntrypoint(String namespace, int ticksPerSecond) {
        super(namespace);
        this.eventDispatcher = new EventDispatcher();
        tickManager = new TickManager(ticksPerSecond);
        manager = new Manager();
        scheduler = new Scheduler();
        tickClock = MutableInstant.ofNow();
        modRegistry = new Registry<>();
        fileSystem = new GameFileSystem();
        routineRegistry = new Registry<>();
        packetRegistry = new PacketRegistry();
        sceneRegistry = new Registry<>();
        localizer = new Localizer(getFileSystem());
    }

    /**
     * The code to be executed every tick
     * This is mainly used for networking tasks, most things for clients should happen every frame
     */
    public void tick() {

    }

    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

    public TickManager getTickManager() {
        return tickManager;
    }

    public Manager getManager() {
        return manager;
    }

    public Scheduler getScheduler() {
        return scheduler;
    }

    public MutableInstant getTickClock() {
        return tickClock;
    }

    public PacketRegistry getPacketRegistry() {
        return packetRegistry;
    }

    public Registry<Mod> getModRegistry() {
        return modRegistry;
    }

    public GameFileSystem getFileSystem() {
        return fileSystem;
    }

    public Registry<Scene> getSceneRegistry() {
        return sceneRegistry;
    }

    public Localizer getLocalizer() {
        return localizer;
    }

    public long getDeltaTime() {
        return deltaTime;
    }

    public Registry<Routine> getRoutineRegistry() {
        return routineRegistry;
    }
}
