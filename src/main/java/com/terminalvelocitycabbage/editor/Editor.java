package com.terminalvelocitycabbage.editor;

import com.terminalvelocitycabbage.editor.registry.*;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.scene.Scene;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.sources.MainSource;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.ResourceCategoryRegistrationEvent;
import com.terminalvelocitycabbage.templates.events.ResourceSourceRegistrationEvent;
import com.terminalvelocitycabbage.templates.events.SceneRegistrationEvent;

import static com.terminalvelocitycabbage.editor.registry.EditorInput.UI_CLICK;
import static com.terminalvelocitycabbage.editor.registry.EditorInput.UI_SCROLL;
import static com.terminalvelocitycabbage.editor.registry.EditorRenderGraphs.EDITOR_RENDER_GRAPH;
import static com.terminalvelocitycabbage.editor.registry.EditorTextures.UI_ATLAS;

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
            ResourceCategory.registerEngineDefaults(((ResourceCategoryRegistrationEvent) event).getRegistry());
        });
        dispatcher.listenToEvent(ResourceSourceRegistrationEvent.EVENT, event -> {
            ResourceSource mainSource = new MainSource(getInstance(), getNamespace());
            mainSource.registerDefaultSources(getNamespace());
            ENGINE_RESOURCE_SOURCE = ((ResourceSourceRegistrationEvent) event).registerResourceSource(getNamespace(), "editor", mainSource);
        });
        dispatcher.listenToEvent(SceneRegistrationEvent.EVENT, event -> {
            EDITOR_SCENE = ((SceneRegistrationEvent) event).registerScene(getNamespace(), "editor",
                    Scene.builder()
                            .inputControllers(UI_SCROLL, UI_CLICK)
                            .renderGraph(EDITOR_RENDER_GRAPH)
                            .textureAtlases(UI_ATLAS)
                            .build());
        });
        EditorFonts.init(dispatcher);
        EditorRenderGraphs.init(dispatcher);
        EditorInput.init(dispatcher);
        EditorTextures.init(dispatcher);
        EditorStates.init(dispatcher);
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
