package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.DataMesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.Vertex;
import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;

public class TVModelDataMesh extends DataMesh {

    private final TVModel model;
    private final String layerName;
    private int[] indices;

    public TVModelDataMesh(TVModel model, String layerName) {
        this.model = model;
        this.layerName = layerName;
    }

    @Override
    public Vertex[] getVertices(VertexFormat format) {
        List<Vertex> vertexList = new ArrayList<>();
        List<Integer> indexList = new ArrayList<>();
        Vector2i textureSize = model.metadata().textureLayers().get(layerName);
        if (textureSize == null) return new Vertex[0];

        for (TVModel.TVModelCube cube : model.cubes().values()) {
            if (cube.textures().layer().equals(layerName)) {
                addCube(cube, vertexList, indexList, format, textureSize);
            }
        }

        this.indices = indexList.stream().mapToInt(i -> i).toArray();
        return vertexList.toArray(new Vertex[0]);
    }

    @Override
    public int[] getIndices() {
        return indices;
    }

    private void addCube(TVModel.TVModelCube cube, List<Vertex> vertices, List<Integer> indices, VertexFormat format, Vector2i textureSize) {
        Matrix4f transform = calculateGlobalTransform(cube);

        float sx = cube.size().x() + cube.grow().x() * 2;
        float sy = cube.size().y() + cube.grow().y() * 2;
        float sz = cube.size().z() + cube.grow().z() * 2;

        float ox = cube.offset().x() - cube.grow().x();
        float oy = cube.offset().y() - cube.grow().y();
        float oz = cube.offset().z() - cube.grow().z();

        // Positive X face (Right)
        cube.textures().pxFace().ifPresent(uv -> addFace(vertices, indices, format, transform,
                new Vector3f(ox + sx, oy, oz + sz),
                new Vector3f(ox + sx, oy, oz),
                new Vector3f(ox + sx, oy + sy, oz),
                new Vector3f(ox + sx, oy + sy, oz + sz),
                uv, textureSize));

        // Negative X face (Left)
        cube.textures().nxFace().ifPresent(uv -> addFace(vertices, indices, format, transform,
                new Vector3f(ox, oy, oz),
                new Vector3f(ox, oy, oz + sz),
                new Vector3f(ox, oy + sy, oz + sz),
                new Vector3f(ox, oy + sy, oz),
                uv, textureSize));

        // Positive Y face (Up)
        cube.textures().pyFace().ifPresent(uv -> addFace(vertices, indices, format, transform,
                new Vector3f(ox, oy + sy, oz + sz),
                new Vector3f(ox + sx, oy + sy, oz + sz),
                new Vector3f(ox + sx, oy + sy, oz),
                new Vector3f(ox, oy + sy, oz),
                uv, textureSize));

        // Negative Y face (Down)
        cube.textures().nyFace().ifPresent(uv -> addFace(vertices, indices, format, transform,
                new Vector3f(ox, oy, oz),
                new Vector3f(ox + sx, oy, oz),
                new Vector3f(ox + sx, oy, oz + sz),
                new Vector3f(ox, oy, oz + sz),
                uv, textureSize));

        // Positive Z face (Forward/South)
        cube.textures().pzFace().ifPresent(uv -> addFace(vertices, indices, format, transform,
                new Vector3f(ox, oy, oz + sz),
                new Vector3f(ox + sx, oy, oz + sz),
                new Vector3f(ox + sx, oy + sy, oz + sz),
                new Vector3f(ox, oy + sy, oz + sz),
                uv, textureSize));

        // Negative Z face (Back/North)
        cube.textures().nzFace().ifPresent(uv -> addFace(vertices, indices, format, transform,
                new Vector3f(ox + sx, oy, oz),
                new Vector3f(ox, oy, oz),
                new Vector3f(ox, oy + sy, oz),
                new Vector3f(ox + sx, oy + sy, oz),
                uv, textureSize));
    }

    private void addFace(List<Vertex> vertices, List<Integer> indices, VertexFormat format, Matrix4f transform,
                        Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3,
                        TVModel.TVModelCubeTextureMapping.TVModelFaceUV uv, Vector2i textureSize) {
        int baseIndex = vertices.size();

        int u1 = uv.u1v1().x;
        int v1_uv = uv.u1v1().y;
        int u2 = uv.u2v2().x;
        int v2_uv = uv.u2v2().y;

        int[] u = {u1, u2, u2, u1};
        int[] v = {v2_uv, v2_uv, v1_uv, v1_uv};

        int rotSteps = (uv.rotation() / 90) % 4;
        for (int i = 0; i < rotSteps; i++) {
            int tempU = u[0];
            int tempV = v[0];
            u[0] = u[1]; v[0] = v[1];
            u[1] = u[2]; v[1] = v[2];
            u[2] = u[3]; v[2] = v[3];
            u[3] = tempU; v[3] = tempV;
        }

        vertices.add(createVertex(format, transform, v0, u[0], v[0], textureSize));
        vertices.add(createVertex(format, transform, v1, u[1], v[1], textureSize));
        vertices.add(createVertex(format, transform, v2, u[2], v[2], textureSize));
        vertices.add(createVertex(format, transform, v3, u[3], v[3], textureSize));

        indices.add(baseIndex);
        indices.add(baseIndex + 1);
        indices.add(baseIndex + 2);
        indices.add(baseIndex + 2);
        indices.add(baseIndex + 3);
        indices.add(baseIndex);
    }

    private Vertex createVertex(VertexFormat format, Matrix4f transform, Vector3f pos, int u, int v, Vector2i textureSize) {
        Vector4f transformedPos = new Vector4f(pos, 1.0f).mul(transform);
        return new Vertex(format)
                .setXYZPosition(transformedPos.x, transformedPos.y, transformedPos.z)
                .setRGBColor(1.0f, 1.0f, 1.0f)
                .setUV((float) u / textureSize.x, (float) v / textureSize.y);
    }

    private Matrix4f calculateGlobalTransform(TVModel.TVModelCube cube) {
        Matrix4f matrix = new Matrix4f();
        List<Object> parents = new ArrayList<>();
        parents.add(cube);

        String parentName = cube.parent().orElse(null);
        while (parentName != null) {
            if (model.bones().containsKey(parentName)) {
                var bone = model.bones().get(parentName);
                parents.add(bone);
                parentName = bone.parent().orElse(null);
            } else if (model.cubes().containsKey(parentName)) {
                var parentCube = model.cubes().get(parentName);
                parents.add(parentCube);
                parentName = parentCube.parent().orElse(null);
            } else {
                parentName = null;
            }
        }

        for (int i = parents.size() - 1; i >= 0; i--) {
            Object p = parents.get(i);
            if (p instanceof TVModel.TVModelBone bone) {
                matrix.translate(bone.position());
                matrix.rotateZYX((float) Math.toRadians(bone.rotation().z()), (float) Math.toRadians(bone.rotation().y()), (float) Math.toRadians(bone.rotation().x()));
            } else if (p instanceof TVModel.TVModelCube c) {
                matrix.translate(c.position());
                matrix.rotateZYX((float) Math.toRadians(c.rotation().z()), (float) Math.toRadians(c.rotation().y()), (float) Math.toRadians(c.rotation().x()));
            }
        }

        return matrix;
    }
}
