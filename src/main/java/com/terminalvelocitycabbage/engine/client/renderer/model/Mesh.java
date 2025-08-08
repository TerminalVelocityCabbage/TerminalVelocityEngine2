package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.materials.Atlas;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.ArrayUtils;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
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

    public Mesh(VertexFormat format, DataMesh dataMesh) {
        this(format, dataMesh.getVertices(format), dataMesh.getIndices());
    }

    public Mesh(Mesh mesh) {
        this.vertices = new Vertex[mesh.vertices.length];
        for (int i = 0; i < mesh.vertices.length; i++) {
            this.vertices[i] = new Vertex(mesh.vertices[i]);
        }
        this.indices = mesh.indices.clone();
        this.format = mesh.format;
    }

    public static Mesh of(List<Mesh> meshes) {

        //Verify all meshes have the same vertex format before merging data
        VertexFormat format1 = null;
        //While we're at it track vertex and index counts for later use IF it makes it past this point
        int vertexCount = 0;
        int indicesCount = 0;
        for (Mesh mesh : meshes) {
            if (format1 != null) {
                if (format1 != mesh.getFormat()) {
                    Log.crash("Tried to construct a mesh with mismatched formats: " + format1 + ", " + mesh.getFormat());
                }
            }
            vertexCount += mesh.getNumVertices();
            indicesCount += mesh.getNumIndices();
            format1 = mesh.getFormat();
        }

        //Combine all vertex data and index data
        Vertex[] vertices = new Vertex[vertexCount];
        int[] indices = new int[indicesCount];
        int meshIndex = 0;
        int vertexIndex = 0;
        int indexOffset = 0;
        for (Mesh mesh : meshes) {
            for (int index : mesh.indices) {
                indices[meshIndex] = index + indexOffset;
                meshIndex++;
            }
            for (Vertex vertex : mesh.vertices) {
                vertices[vertexIndex] = vertex;
                vertexIndex++;
            }
            indexOffset += mesh.getNumVertices();
        }

        return new Mesh(format1, vertices, indices);
    }

    /**
     * Initializes this mesh to be rendered. Only needs to be called once
     */
    public void init() {

        //Just in case it makes it to this point (it shouldn't)
        if (vertices.length == 0) {
            Log.error("Tried to initialize an empty mesh.");
            return;
        }

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
            FloatBuffer attributeBuffer = MemoryUtil.memCallocFloat(attributeData.length);
            try {
                attributeBuffer.put(0, attributeData);
                //Upload this data to OpenGL
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, attributeBuffer, GL_STATIC_DRAW);
                glEnableVertexAttribArray(attributeIndex);
                glVertexAttribPointer(attributeIndex, attribute.getNumComponents(), GL_FLOAT, attribute.isNormalized(), 0, 0);
            } finally {
                MemoryUtil.memFree(attributeBuffer);
            }
            attributeIndex++;
        }

        //Create the index buffers for mesh rendering
        vboId = glGenBuffers();
        //Upload the data to the buffer and opengl
        vboIdList.add(vboId);
        IntBuffer indicesBuffer = MemoryUtil.memAllocInt(indices.length);
        try {
            indicesBuffer.put(0, indices);
            glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId);
            glBufferData(GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL_STATIC_DRAW);
        } finally {
            MemoryUtil.memFree(indicesBuffer);
        }

        //Bind all buffers
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        //Mark this mesh as initialized, so we don't have to do this twice
        initialized = true;
    }

    /**
     * Renders this mesh
     */
    public void render() {
        if (vertices.length == 0) return;
        //TODO move this initialization to scenes so that it doesn't hang on first time rendering something
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

    public float[] getVertexData() {
        List<Float> data = new ArrayList<>();
        for (Vertex vertex : this.vertices) {
            for (float value : vertex.getData()) {
                data.add(value);
            }
        }
        var floatData = new float[data.size()];
        for (int i = 0; i < data.size(); i++) {
            floatData[i] = data.get(i);
        }
        return floatData;
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

    /**
     * Transforms this mesh's current UVS from a single texture UV to an atlas UV
     * @param atlas The atlas that the UVs need to be transformed from
     * @param textureIdentifier The texture that the UVs were originally intended for
     */
    public void transformUVsByAtlas(Atlas atlas, Identifier textureIdentifier) {
        for (Vertex vertex : vertices) {
            float[] data = vertex.getSubData(VertexAttribute.UV);
            for (int i = 0; i < data.length; i+=2) {
                var newUV = atlas.getTextureUVFromModelUV(textureIdentifier, new Vector2f(data[i], data[i+1]));
                vertex.setUV(newUV.x, newUV.y);
            }
        }
    }

    public void dumpAsObj() {
        try (PrintStream stream = new PrintStream(new FileOutputStream("./dump.obj"))) {

            int stride = this.format.getNumComponents();
            float[] vertex = getVertexData();
            int[] index = indices;

            int pos = this.format.getOffset(VertexAttribute.XYZ_POSITION);
            int uv = this.format.getOffset(VertexAttribute.UV);
            int normal = this.format.getOffset(VertexAttribute.XYZ_NORMAL);

            for (int i = 0; i < vertex.length; i += stride) {
                stream.println("v " + vertex[i+pos] + " " + vertex[i+pos+1] + " " + vertex[i+pos+2]);
                if(uv != -1) {
                    stream.println("vt " + vertex[i+uv] + " " + vertex[i+uv+1]);
                }
                if(normal != -1) {
                    stream.println("vn " + vertex[i+normal] + " " + vertex[i+normal+1] + " " + vertex[i+normal+2]);
                }
            }

            for (int i = 0; i < index.length; i+=3) {
                stream.println("f " + (index[i]+1) + " " + (index[i+1]+1) + " " + (index[i+2]+1));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}