package com.terminalvelocitycabbage.editor;

import com.terminalvelocitycabbage.editor.registry.*;
import com.terminalvelocitycabbage.editor.scenes.EditorScene;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.sources.MainSource;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.ResourceCategoryRegistrationEvent;
import com.terminalvelocitycabbage.templates.events.ResourceSourceRegistrationEvent;
import com.terminalvelocitycabbage.templates.events.SceneRegistrationEvent;

public abstract class Editor<T extends ClientBase> extends ClientBase {

    public static Identifier ENGINE_RESOURCE_SOURCE;

    public static Identifier EDITOR_SCENE;

    protected T gameClient;

    public static final String ID = "editor";

    public Editor(T gameClient) {
        super(ID, 20);
        this.gameClient = gameClient;
    }

    @Override
    public void registerEventListeners(EventDispatcher dispatcher) {
        gameClient.registerEventListeners(dispatcher);
        dispatcher.listenToEvent(ResourceCategoryRegistrationEvent.EVENT, event -> {
            ResourceCategory.registerEngineDefaults(((ResourceCategoryRegistrationEvent) event).getRegistry(), getNamespace());
        });
        dispatcher.listenToEvent(ResourceSourceRegistrationEvent.EVENT, event -> {
            ResourceSource mainSource = new MainSource(getInstance(), getNamespace());
            mainSource.registerDefaultSources(getNamespace());
            ENGINE_RESOURCE_SOURCE = ((ResourceSourceRegistrationEvent) event).registerResourceSource(getNamespace(), "editor", mainSource);
        });
        dispatcher.listenToEvent(SceneRegistrationEvent.EVENT, event -> {
            EDITOR_SCENE = ((SceneRegistrationEvent) event).registerScene(getNamespace(), "editor", new EditorScene());
        });
        EditorFonts.init(getEventDispatcher());
        EditorRenderGraphs.init(getEventDispatcher());
        EditorInput.init(getEventDispatcher());
        EditorTextures.init(getEventDispatcher());
        EditorStates.init(getEventDispatcher());
    }

    @Override
    public void init() {
        super.init();
        WindowProperties properties = new WindowProperties(1800, 900, "Terminal Velocity Engine", EDITOR_SCENE);
        long window = getWindowManager().createNewWindow(properties);
        getWindowManager().focusWindow(window);
    }

    public T getGameClient() {
        return gameClient;
    }
}
