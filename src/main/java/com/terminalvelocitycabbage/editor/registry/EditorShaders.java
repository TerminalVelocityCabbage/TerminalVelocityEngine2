package com.terminalvelocitycabbage.editor.registry;

import com.terminalvelocitycabbage.editor.Editor;
import com.terminalvelocitycabbage.engine.client.renderer.shader.Shader;
import com.terminalvelocitycabbage.engine.client.renderer.shader.ShaderProgramConfig;
import com.terminalvelocitycabbage.engine.client.renderer.shader.Uniform;
import com.terminalvelocitycabbage.engine.registry.Identifier;

import static com.terminalvelocitycabbage.editor.data.EditorMeshData.ANIMATED_MESH_FORMAT;
import static com.terminalvelocitycabbage.editor.data.EditorMeshData.MESH_FORMAT;
import static com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory.SHADER;

public class EditorShaders {

    public static final Identifier TEST_VERTEX_SHADER = SHADER.identifierOf(Editor.ID, "default_vertex");
    public static final Identifier ANIMATED_VERTEX_SHADER = SHADER.identifierOf(Editor.ID, "animated_vertex");
    public static final Identifier TEST_FRAGMENT_SHADER = SHADER.identifierOf(Editor.ID, "default_fragment");
    public static final ShaderProgramConfig MESH_SHADER_PROGRAM_CONFIG = ShaderProgramConfig.builder()
            .vertexFormat(MESH_FORMAT)
            .addShader(Shader.Type.VERTEX, EditorShaders.TEST_VERTEX_SHADER)
            .addShader(Shader.Type.FRAGMENT, EditorShaders.TEST_FRAGMENT_SHADER)
            .addUniform(new Uniform("textureSampler"))
            .addUniform(new Uniform("projectionMatrix"))
            .addUniform(new Uniform("viewMatrix"))
            .addUniform(new Uniform("modelMatrix"))
            .addUniform(new Uniform("directionalLight"))
            .build();

    public static final ShaderProgramConfig ANIMATED_MESH_SHADER_PROGRAM_CONFIG = ShaderProgramConfig.builder()
            .vertexFormat(ANIMATED_MESH_FORMAT)
            .addShader(Shader.Type.VERTEX, EditorShaders.ANIMATED_VERTEX_SHADER)
            .addShader(Shader.Type.FRAGMENT, EditorShaders.TEST_FRAGMENT_SHADER)
            .addUniform(new Uniform("textureSampler"))
            .addUniform(new Uniform("projectionMatrix"))
            .addUniform(new Uniform("viewMatrix"))
            .addUniform(new Uniform("modelMatrix"))
            .addUniform(new Uniform("boneMatrices"))
            .addUniform(new Uniform("directionalLight"))
            .build();

}
