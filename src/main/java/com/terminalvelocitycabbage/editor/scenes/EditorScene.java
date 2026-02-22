package com.terminalvelocitycabbage.editor.scenes;

import com.terminalvelocitycabbage.editor.registry.EditorRenderGraphs;
import com.terminalvelocitycabbage.editor.registry.EditorTextures;
import com.terminalvelocitycabbage.engine.client.scene.Scene;

import java.util.List;

public class EditorScene extends Scene {

    public EditorScene() {
        super(EditorRenderGraphs.EDITOR_RENDER_GRAPH, List.of());
    }

    @Override
    public void init() {
        EditorTextures.generateAtlases();
    }

    @Override
    public void cleanup() {

    }
}
