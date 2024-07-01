package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.util.ArrayUtils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    private final Vertex[] vertices;
    private final int[] indices;
    VertexFormat format;
    private int vaoId;
    private List<Integer> vboIdList;
    boolean initialized = false;

    public Mesh(VertexFormat format, Vertex[] vertices, int[] indices) {
        //Make sure that all vertices have enough values for the specified format
        for (Vertex vertex : vertices) {
            if (format.getNumComponents() != vertex.getData(format).length) Log.crash("Vertex data does not match specified mesh format");
        }

        this.format = format;
        this.vertices = vertices;
        this.indices = indices;
    }

    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            int vboId;

            // Attribute VBOs
            for (int i = 0; i < format.getNumElements(); i++) {
                VertexAttribute attribute = format.getElement(i);

                vboId = glGenBuffers();
                vboIdList.add(vboId);
                var attributeData = getDataOfType(attribute);
                FloatBuffer attributeBuffer = stack.callocFloat(attributeData.length);
                attributeBuffer.put(0, attributeData);
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, attributeBuffer, GL_STATIC_DRAW);
                glEnableVertexAttribArray(i);
                glVertexAttribPointer(i, attribute.getNumComponents(), GL_FLOAT, attribute.isNormalized(), 0, 0);
            }

            //Index VBO
            vboId = glGenBuffers();
            vboIdList.add(vboId);
            IntBuffer indicesBuffer = stack.callocInt(indices.length);
            indicesBuffer.put(0, indices);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);

            //Bind all buffers
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        initialized = true;
    }

    public void render() {
        if (!isInitialized()) init();
        glBindVertexArray(getVaoId());
        glDrawElements(GL_TRIANGLES, getNumIndices(), GL_UNSIGNED_INT, 0);
    }

    private float[] getDataOfType(VertexAttribute element) {
        List<float[]> vertices = new ArrayList<>();
        for (Vertex vertex : this.vertices) {
            vertices.add(vertex.getSubData(element));
        }
        return ArrayUtils.combineFloatArrays(vertices);
    }

    public void cleanup() {
        vboIdList.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(getVaoId());
    }

    public int getNumVertices() {
        return vertices.length;
    }

    private int getNumIndices() {
        return indices.length;
    }

    public final int getVaoId() {
        return vaoId;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public VertexFormat getFormat() {
        return format;
    }
}
