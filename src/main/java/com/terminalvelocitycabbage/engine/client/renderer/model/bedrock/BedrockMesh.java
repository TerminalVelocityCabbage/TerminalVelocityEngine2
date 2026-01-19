package com.terminalvelocitycabbage.engine.client.renderer.model.bedrock;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.DataMesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.Vertex;
import com.terminalvelocitycabbage.engine.debug.Log;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * A specialized {@link DataMesh} that converts Minecraft Bedrock Edition geometry data
 * into a renderable mesh for the Terminal Velocity Engine.
 * 
 * It handles the creation of cubes, calculation of UVs from Bedrock's format,
 * and assignment of bone indices for shader-side skeletal animation.
 */
public class BedrockMesh extends DataMesh {

    private final List<Vertex> vertices;
    private final List<Integer> indices;
    private final BedrockGeometry geometry;

    /**
     * Constructs a new BedrockMesh from the given geometry.
     * @param geometry The parsed Bedrock geometry data.
     */
    public BedrockMesh(BedrockGeometry geometry) {
        this.geometry = geometry;
        this.vertices = new ArrayList<>();
        this.indices = new ArrayList<>();
        compile();
    }

    public BedrockGeometry getGeometry() {
        return geometry;
    }

    /**
     * Compiles the Bedrock geometry into a list of vertices and indices.
     */
    private void compile() {
        if (geometry.minecraft_geometry == null || geometry.minecraft_geometry.isEmpty()) return;
        var data = geometry.minecraft_geometry.get(0);
        float tw = data.description.texture_width;
        float th = data.description.texture_height;

        for (int i = 0; i < data.bones.size(); i++) {
            var bone = data.bones.get(i);
            if (bone.cubes == null) continue;
            for (var cube : bone.cubes) {
                addCube(cube, bone, i, tw, th);
            }
        }
    }

    private float getFloat(List<?> list, int index) {
        Object val = list.get(index);
        if (val instanceof Number n) return n.floatValue();
        return 0;
    }

    /**
     * Adds a single cube from the Bedrock geometry to this mesh.
     * 
     * @param cube The cube data
     * @param bone The bone this cube belongs to
     * @param boneIndex The index of the bone in the palette
     * @param tw Texture width (for UV normalization)
     * @param th Texture height (for UV normalization)
     */
    private void addCube(BedrockGeometry.Cube cube, BedrockGeometry.Bone bone, int boneIndex, float tw, float th) {
        float x = getFloat(cube.origin, 0);
        float y = getFloat(cube.origin, 1);
        float z = getFloat(cube.origin, 2);
        float sx = getFloat(cube.size, 0);
        float sy = getFloat(cube.size, 1);
        float sz = getFloat(cube.size, 2);
        float inflate = cube.inflate;

        // Bone pivot
        float bx = getFloat(bone.pivot, 0);
        float by = getFloat(bone.pivot, 1);
        float bz = getFloat(bone.pivot, 2);

        // Relative to bone pivot
        float x0 = x - bx - inflate;
        float y0 = y - by - inflate;
        float z0 = z - bz - inflate;
        float x1 = x0 + sx + 2 * inflate;
        float y1 = y0 + sy + 2 * inflate;
        float z1 = z0 + sz + 2 * inflate;

        Log.info("Cube: " + x0 + " " + y0 + " " + z0 + " size: " + sx + " " + sy + " " + sz + " inflate: " + inflate);

        // Vertices (8 corners)
        // We'll add 6 faces, each with 4 vertices

        // Define faces: +x, -x, +y, -y, +z, -z
        // Bedrock faces: north (-z), south (+z), west (-x), east (+x), up (+y), down (-y)

        if (cube.uv != null) {
            // North (-z)
            addFace(new Vector3f(x1, y1, z0), new Vector3f(x0, y1, z0), new Vector3f(x0, y0, z0), new Vector3f(x1, y0, z0),
                    new Vector3f(0, 0, -1), boneIndex, cube.uv.get("north"), tw, th);
            // South (+z)
            addFace(new Vector3f(x0, y1, z1), new Vector3f(x1, y1, z1), new Vector3f(x1, y0, z1), new Vector3f(x0, y0, z1),
                    new Vector3f(0, 0, 1), boneIndex, cube.uv.get("south"), tw, th);
            // West (-x)
            addFace(new Vector3f(x0, y1, z0), new Vector3f(x0, y1, z1), new Vector3f(x0, y0, z1), new Vector3f(x0, y0, z0),
                    new Vector3f(-1, 0, 0), boneIndex, cube.uv.get("west"), tw, th);
            // East (+x)
            addFace(new Vector3f(x1, y1, z1), new Vector3f(x1, y1, z0), new Vector3f(x1, y0, z0), new Vector3f(x1, y0, z1),
                    new Vector3f(1, 0, 0), boneIndex, cube.uv.get("east"), tw, th);
            // Up (+y)
            addFace(new Vector3f(x0, y1, z1), new Vector3f(x0, y1, z0), new Vector3f(x1, y1, z0), new Vector3f(x1, y1, z1),
                    new Vector3f(0, 1, 0), boneIndex, cube.uv.get("up"), tw, th);
            // Down (-y)
            addFace(new Vector3f(x0, y0, z0), new Vector3f(x0, y0, z1), new Vector3f(x1, y0, z1), new Vector3f(x1, y0, z0),
                    new Vector3f(0, -1, 0), boneIndex, cube.uv.get("down"), tw, th);
        } else if (cube.uv_list != null) {
            float u = getFloat(cube.uv_list, 0);
            float v = getFloat(cube.uv_list, 1);

            // Up (+y)
            addFace(new Vector3f(x0, y1, z1), new Vector3f(x0, y1, z0), new Vector3f(x1, y1, z0), new Vector3f(x1, y1, z1),
                    new Vector3f(0, 1, 0), boneIndex, u + sz, v, sx, sz, tw, th);
            // Down (-y)
            addFace(new Vector3f(x0, y0, z0), new Vector3f(x0, y0, z1), new Vector3f(x1, y0, z1), new Vector3f(x1, y0, z0),
                    new Vector3f(0, -1, 0), boneIndex, u + sz + sx, v, sx, sz, tw, th);
            // North (-z)
            addFace(new Vector3f(x1, y1, z0), new Vector3f(x0, y1, z0), new Vector3f(x0, y0, z0), new Vector3f(x1, y0, z0),
                    new Vector3f(0, 0, -1), boneIndex, u + sz + sx, v + sz, sx, sy, tw, th);
            // East (+x)
            addFace(new Vector3f(x1, y1, z1), new Vector3f(x1, y1, z0), new Vector3f(x1, y0, z0), new Vector3f(x1, y0, z1),
                    new Vector3f(1, 0, 0), boneIndex, u, v + sz, sz, sy, tw, th);
            // South (+z)
            addFace(new Vector3f(x0, y1, z1), new Vector3f(x1, y1, z1), new Vector3f(x1, y0, z1), new Vector3f(x0, y0, z1),
                    new Vector3f(0, 0, 1), boneIndex, u + sz + sx + sz, v + sz, sx, sy, tw, th);
            // West (-x)
            addFace(new Vector3f(x0, y1, z0), new Vector3f(x0, y1, z1), new Vector3f(x0, y0, z1), new Vector3f(x0, y0, z0),
                    new Vector3f(-1, 0, 0), boneIndex, u + sz, v + sz, sz, sy, tw, th);
        }
    }

    private void addFace(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f normal, int boneIndex, BedrockGeometry.UVMappedFace uv, float tw, float th) {
        if (uv == null) return;
        addFace(p0, p1, p2, p3, normal, boneIndex, getFloat(uv.uv, 0), getFloat(uv.uv, 1), getFloat(uv.uv_size, 0), getFloat(uv.uv_size, 1), tw, th);
    }

    private void addFace(Vector3f p0, Vector3f p1, Vector3f p2, Vector3f p3, Vector3f normal, int boneIndex, float u, float v, float uw, float vh, float tw, float th) {

        int baseIdx = vertices.size();

        float u0 = u / tw;
        float v0 = v / th;
        float duw = uw / tw;
        float dvh = vh / th;

        VertexFormat format = VertexFormat.builder()
                .addElement(VertexAttribute.XYZ_POSITION)
                .addElement(VertexAttribute.UV)
                .addElement(VertexAttribute.XYZ_NORMAL)
                .addElement(VertexAttribute.BONE_INDICES)
                .build();

        vertices.add(new Vertex(format).setXYZPosition(p0.x, p0.y, p0.z).setUV(u0 + duw, v0).setXYZNormal(normal.x, normal.y, normal.z).setBoneIndices(boneIndex, 0, 0, 0));
        vertices.add(new Vertex(format).setXYZPosition(p1.x, p1.y, p1.z).setUV(u0, v0).setXYZNormal(normal.x, normal.y, normal.z).setBoneIndices(boneIndex, 0, 0, 0));
        vertices.add(new Vertex(format).setXYZPosition(p2.x, p2.y, p2.z).setUV(u0, v0 + dvh).setXYZNormal(normal.x, normal.y, normal.z).setBoneIndices(boneIndex, 0, 0, 0));
        vertices.add(new Vertex(format).setXYZPosition(p3.x, p3.y, p3.z).setUV(u0 + duw, v0 + dvh).setXYZNormal(normal.x, normal.y, normal.z).setBoneIndices(boneIndex, 0, 0, 0));

        indices.add(baseIdx);
        indices.add(baseIdx + 1);
        indices.add(baseIdx + 2);
        indices.add(baseIdx);
        indices.add(baseIdx + 2);
        indices.add(baseIdx + 3);
    }

    @Override
    public Vertex[] getVertices(VertexFormat format) {
        // We actually used a fixed format in addFace, but Mesh might want to convert it.
        // For now, let's assume the format matches or handle conversion if needed.
        return vertices.toArray(new Vertex[0]);
    }

    @Override
    public int[] getIndices() {
        return indices.stream().mapToInt(i -> i).toArray();
    }
}
