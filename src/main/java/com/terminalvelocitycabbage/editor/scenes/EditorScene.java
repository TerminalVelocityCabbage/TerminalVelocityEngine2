package com.terminalvelocitycabbage.editor.scenes;

import com.terminalvelocitycabbage.editor.registry.EditorInput;
import com.terminalvelocitycabbage.editor.registry.EditorRenderGraphs;
import com.terminalvelocitycabbage.editor.registry.EditorTextures;
import com.terminalvelocitycabbage.engine.client.scene.Scene;

import java.util.List;

public class EditorScene extends Scene {

    public EditorScene() {
        super(EditorRenderGraphs.EDITOR_RENDER_GRAPH, List.of());
        addInputControllers(EditorInput.UI_CLICK, EditorInput.UI_SCROLL);
    }

    @Override
    public void init() {
        EditorTextures.generateAtlases();
    }

    @Override
    public void cleanup() {

    }
}
