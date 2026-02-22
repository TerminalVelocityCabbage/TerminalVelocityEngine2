package com.terminalvelocitycabbage.editor;

import com.terminalvelocitycabbage.editor.registry.EditorFonts;
import com.terminalvelocitycabbage.editor.registry.EditorInput;
import com.terminalvelocitycabbage.editor.registry.EditorRenderGraphs;
import com.terminalvelocitycabbage.editor.registry.EditorTextures;
import com.terminalvelocitycabbage.editor.scenes.EditorScene;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.window.WindowProperties;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceSource;
import com.terminalvelocitycabbage.engine.filesystem.sources.MainSource;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.ResourceCategoryRegistrationEvent;
import com.terminalvelocitycabbage.templates.events.ResourceSourceRegistrationEvent;
import com.terminalvelocitycabbage.templates.events.SceneRegistrationEvent;

public class TerminalVelocityEngineEditor extends ClientBase {

    public static final String ID = "editor";

    public static Identifier ENGINE_RESOURCE_SOURCE;

    public static Identifier EDITOR_SCENE;

    public TerminalVelocityEngineEditor() {
        super(ID, 20);

        getEventDispatcher().listenToEvent(ResourceCategoryRegistrationEvent.EVENT, event -> {
            ResourceCategory.registerEngineDefaults(((ResourceCategoryRegistrationEvent) event).getRegistry(), ID);
        });
        getEventDispatcher().listenToEvent(ResourceSourceRegistrationEvent.EVENT, event -> {
            ResourceSource mainSource = new MainSource(getInstance());
            mainSource.registerDefaultSources(ID);
            ENGINE_RESOURCE_SOURCE = ((ResourceSourceRegistrationEvent) event).registerResourceSource(ID, "main", mainSource);
        });
        getEventDispatcher().listenToEvent(SceneRegistrationEvent.EVENT, event -> {
            EDITOR_SCENE = ((SceneRegistrationEvent) event).registerScene(ID, "editor", new EditorScene());
        });
        EditorFonts.init(getEventDispatcher());
        EditorRenderGraphs.init(getEventDispatcher());
        EditorInput.init(getEventDispatcher());
        EditorTextures.init(getEventDispatcher());
    }

    public static void main(String[] args) {
        var editor = new TerminalVelocityEngineEditor();
        editor.start();
    }

    @Override
    public void init() {
        super.init();

        WindowProperties properties = new WindowProperties(1800, 900, "Terminal Velocity Engine", EDITOR_SCENE);
        long window = getWindowManager().createNewWindow(properties);
        getWindowManager().focusWindow(window);
    }
}
