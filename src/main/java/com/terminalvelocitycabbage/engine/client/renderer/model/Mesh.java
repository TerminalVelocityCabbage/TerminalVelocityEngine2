package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexElement;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.util.ArrayUtils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {

    private final Vertex[] vertices;
    VertexFormat format;
    private int vaoId;
    private List<Integer> vboIdList;
    boolean initialized = false;

    public Mesh(VertexFormat format, Vertex[] vertices) {
        //Make sure that all vertices have enough values for the specified format
        for (Vertex vertex : vertices) {
            if (format.getNumComponents() != vertex.getData().length) Log.crash("Vertex data does not match specified mesh format");
        }

        this.format = format;
        this.vertices = vertices;
    }

    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            // Positions VBO
            if (format.containsElement(VertexElement.XYZ_POSITION)) {
                int vboId = glGenBuffers();
                vboIdList.add(vboId);
                FloatBuffer positionsBuffer = stack.callocFloat(VertexElement.XYZ_POSITION.getNumComponents() * vertices.length);
                positionsBuffer.put(0, getDataOfType(VertexElement.XYZ_POSITION));
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, positionsBuffer, GL_STATIC_DRAW);
                glEnableVertexAttribArray(0);
                glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            }

            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
        initialized = true;
    }

    public void render() {
        if (!isInitialized()) init();
        glBindVertexArray(getVaoId());
        glDrawArrays(GL_TRIANGLES, 0, getNumVertices());
    }

    private float[] getDataOfType(VertexElement element) {
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

    public final int getVaoId() {
        return vaoId;
    }

    public boolean isInitialized() {
        return initialized;
    }
}
