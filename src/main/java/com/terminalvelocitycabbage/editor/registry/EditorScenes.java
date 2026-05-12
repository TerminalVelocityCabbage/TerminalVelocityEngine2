package com.terminalvelocitycabbage.editor.registry;

import com.terminalvelocitycabbage.editor.Editor;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.SceneRegistrationEvent;

public class EditorScenes {

    public static Identifier EDITOR_SCENE;

    public static void init(SceneRegistrationEvent event) {
        EDITOR_SCENE = event.registerSceneFromFile(Editor.ID, "editor");
    }

}
