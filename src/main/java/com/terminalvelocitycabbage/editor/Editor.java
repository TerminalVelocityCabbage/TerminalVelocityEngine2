package com.terminalvelocitycabbage.editor;

import com.terminalvelocitycabbage.editor.registry.*;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.sources.MainSource;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.*;

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

        dispatcher.listenToEvent(ResourceCategoryRegistrationEvent.EVENT, event -> EditorResources.registerResourceCategories((ResourceCategoryRegistrationEvent) event));
        dispatcher.listenToEvent(ResourceSourceRegistrationEvent.EVENT, event -> {
            ResourceSource mainSource = new MainSource(getInstance(), getNamespace());
            mainSource.registerDefaultSources(getNamespace());
            ENGINE_RESOURCE_SOURCE = ((ResourceSourceRegistrationEvent) event).registerResourceSource(getNamespace(), "editor", mainSource);
        });
        dispatcher.listenToEvent(EntityComponentRegistrationEvent.EVENT, event -> EditorEntities.registerComponents((EntityComponentRegistrationEvent) event));
        dispatcher.listenToEvent(EntitySystemRegistrationEvent.EVENT, event -> EditorEntities.createSystems((EntitySystemRegistrationEvent) event));
        dispatcher.listenToEvent(RoutineRegistrationEvent.EVENT, event -> EditorRoutines.init((RoutineRegistrationEvent) event));
        dispatcher.listenToEvent(RendererRegistrationEvent.EVENT, event -> EditorRenderers.init((RendererRegistrationEvent) event));
        dispatcher.listenToEvent(SceneRegistrationEvent.EVENT, event -> {
            EditorScenes.init((SceneRegistrationEvent) event);
            EDITOR_SCENE = EditorScenes.EDITOR_SCENE;
        });
        dispatcher.listenToEvent(LocalizedTextKeyRegistrationEvent.EVENT, event -> EditorLocalizedTexts.registerLocalizedTextKeys((LocalizedTextKeyRegistrationEvent) event));
        dispatcher.listenToEvent(MeshRegistrationEvent.EVENT, event -> EditorMeshes.init((MeshRegistrationEvent) event));
        dispatcher.listenToEvent(AnimationConfigurationEvent.EVENT, event -> EditorModels.initAnimations((AnimationConfigurationEvent) event));
        dispatcher.listenToEvent(ModelConfigRegistrationEvent.EVENT, event -> EditorModels.init((ModelConfigRegistrationEvent) event));

        EditorFonts.init(dispatcher);
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
