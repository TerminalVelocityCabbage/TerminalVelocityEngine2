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
            if (format.getNumComponents() != vertex.getData().length) Log.crash("Vertex data does not match specified mesh format");
        }

        this.format = format;
        this.vertices = vertices;
        this.indices = indices;
    }

    /**
     * Initializes this mesh to be rendered. Only needs to be called once
     */
    public void init() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            vboIdList = new ArrayList<>();

            vaoId = glGenVertexArrays();
            glBindVertexArray(vaoId);

            int vboId;

            //Create attribute vbos and upload the data from the mesh
            int attributeIndex = 0;
            //Loop through all the attributes for the format of this mesh
            for (VertexAttribute attribute : format.getAttributes()) {
                //Create a vbo for this attribute data
                vboId = glGenBuffers();
                vboIdList.add(vboId);
                //Get the attribute data from this mesh
                var attributeData = getDataOfType(attribute);
                FloatBuffer attributeBuffer = stack.callocFloat(attributeData.length);
                attributeBuffer.put(0, attributeData);
                //Upload this data to OpenGL
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, attributeBuffer, GL_STATIC_DRAW);
                glEnableVertexAttribArray(attributeIndex);
                glVertexAttribPointer(attributeIndex, attribute.getNumComponents(), GL_FLOAT, attribute.isNormalized(), 0, 0);
                attributeIndex++;
            }

            //Create the index buffers for mesh rendering
            vboId = glGenBuffers();
            //Upload the data to the buffer and opengl
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

    /**
     * Renders this mesh
     */
    public void render() {
        if (!isInitialized()) init();
        glBindVertexArray(getVaoId());
        glDrawElements(GL_TRIANGLES, getNumIndices(), GL_UNSIGNED_INT, 0);
    }

    /**
     * @param element The vertex element that you want the data for from this mesh
     * @return a float array of the mesh data
     */
    private float[] getDataOfType(VertexAttribute element) {
        List<float[]> vertices = new ArrayList<>();
        for (Vertex vertex : this.vertices) {
            vertices.add(vertex.getSubData(element));
        }
        return ArrayUtils.combineFloatArrays(vertices);
    }

    /**
     * cleans up all the buffers and releases them
     */
    public void cleanup() {
        vboIdList.forEach(GL30::glDeleteBuffers);
        glDeleteVertexArrays(getVaoId());
    }

    /**
     * @return The number of vertices that this mesh has
     */
    public int getNumVertices() {
        return vertices.length;
    }

    /**
     * @return The length of the index array
     */
    private int getNumIndices() {
        return indices.length;
    }

    /**
     * @return The opengl id given to this mesh
     */
    public final int getVaoId() {
        return vaoId;
    }

    /**
     * @return whether this mesh has been initialized
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * @return The vertex format of this mesh
     */
    public VertexFormat getFormat() {
        return format;
    }
}
