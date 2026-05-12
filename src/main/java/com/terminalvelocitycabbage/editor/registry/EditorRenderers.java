package com.terminalvelocitycabbage.editor.registry;

import com.terminalvelocitycabbage.editor.Editor;
import com.terminalvelocitycabbage.editor.rendernodes.DrawEditorUIRenderNode;
import com.terminalvelocitycabbage.editor.rendernodes.EditorDrawSceneRenderNode;
import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.RendererRegistrationEvent;

public class EditorRenderers {

    public static Identifier DRAW_SCENE_RENDER_NODE;
    public static Identifier DRAW_EDITOR_UI_RENDER_NODE;

    public static Identifier EDITOR_RENDER_GRAPH;

    public static void init(RendererRegistrationEvent event) {

        DRAW_SCENE_RENDER_NODE = event.registerNode(Editor.ID, "draw_scene");
        DRAW_EDITOR_UI_RENDER_NODE = event.registerNode(Editor.ID, "draw_editor_ui");

        EDITOR_RENDER_GRAPH = event.registerGraph(Editor.ID, "draw_scene",
                new RenderGraph(RenderGraph.RenderPath.builder()
                        .addRenderNode(DRAW_SCENE_RENDER_NODE, EditorDrawSceneRenderNode.class, EditorShaders.MESH_SHADER_PROGRAM_CONFIG)
                        .addRenderNode(DRAW_EDITOR_UI_RENDER_NODE, DrawEditorUIRenderNode.class, ShaderProgramConfig.EMPTY)
                )
        );
    }

}
