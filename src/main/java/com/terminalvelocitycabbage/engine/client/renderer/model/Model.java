package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Model {

    VertexFormat format;
    List<Part> parts;
    Map<String, Integer> boneIndexMap;
    boolean compiledMesh;
    Mesh mesh;

    public Model(VertexFormat format, List<Part> parts, Map<String, Integer> boneIndexMap, Mesh mesh) {
        this(format, parts, boneIndexMap);
        this.mesh = mesh;
        this.compiledMesh = true;
    }

    public Model(VertexFormat format, List<Part> parts, Map<String, Integer> boneIndexMap) {
        this.format = format;
        this.parts = parts;
        this.boneIndexMap = boneIndexMap;
        this.compiledMesh = false;
    }

    public void render() {
        if (compiledMesh) {
            mesh.render();
        } else {
            for (Part part : parts) {
                part.render();
            }
        }
    }

    public void cleanup() {
        for (Part part : parts) {
            part.cleanup();
        }
    }

    public VertexFormat getFormat() {
        return format;
    }

    public List<Part> getParts() {
        return parts;
    }

    public Map<String, Integer> getBoneIndexMap() {
        return boneIndexMap;
    }

    public static class Part {

        String name;
        Part parent;
        List<Part> children;
        Mesh mesh;
        int boneIndex;

        boolean dirty;

        Vector3f pivotPoint;
        Vector3f origin;
        Quaternionf rotation;
        Vector3f scale;

        public Part(String name, Part parent, Mesh mesh, Vector3f pivotPoint, Quaternionf rotation, int boneIndex) {
            this.name = name;
            this.parent = parent;
            this.children = new ArrayList<>();
            this.mesh = mesh;

            this.dirty = true;

            this.pivotPoint = pivotPoint;
            this.rotation = rotation;

            this.boneIndex = boneIndex;
        }

        public void render() {
            if (mesh != null) mesh.render();
            for (Part child : children) {
                child.render();
            }
        }

        public void cleanup() {
            mesh.cleanup();
            for (Part child : children) {
                child.cleanup();
            }
        }

        public void addChild(Part child) {
            children.add(child);
        }

        public Part getParent() {
            return parent;
        }

        public List<Part> getChildren() {
            return children;
        }

        public Mesh getMesh() {
            return mesh;
        }

        public String getName() {
            return name;
        }
    }

}
