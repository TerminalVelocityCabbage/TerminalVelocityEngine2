package com.terminalvelocitycabbage.editor.registry;

import com.terminalvelocitycabbage.editor.rendernodes.DrawEditorUIRenderNode;
import com.terminalvelocitycabbage.engine.client.renderer.RenderGraph;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.event.EventDispatcher;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.templates.events.RendererRegistrationEvent;

import static com.terminalvelocitycabbage.editor.TerminalVelocityEngineEditor.ID;

public class EditorRenderGraphs {

    public static Identifier EDITOR_RENDER_GRAPH;

    public static void init(EventDispatcher eventDispatcher) {

        eventDispatcher.listenToEvent(RendererRegistrationEvent.EVENT, e -> {
            RendererRegistrationEvent event = (RendererRegistrationEvent) e;

            var rootPath = RenderGraph.RenderPath.builder()
                    .addRenderNode(event.registerNode(ID, "draw_editor_ui"), DrawEditorUIRenderNode.class, ShaderProgramConfig.EMPTY);

            EDITOR_RENDER_GRAPH = event.registerGraph(ID, "editor_render_graph", new RenderGraph(rootPath));
        });
    }

}
