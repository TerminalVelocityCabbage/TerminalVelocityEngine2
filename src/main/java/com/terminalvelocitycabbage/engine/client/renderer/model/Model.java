package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.debug.Log;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Model {

    VertexFormat format;
    List<Part> parts;

    public Model(VertexFormat format, List<Part> parts) {
        this.format = format;
        this.parts = parts;
    }

    public void render() {
        for (Part part : parts) {
            part.render();
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

    public static class Part {

        String name;
        Part parent;
        List<Part> children;
        Mesh mesh;

        boolean dirty;

        Vector3f pivotPoint;
        Vector3f origin;
        Quaternionf rotation;
        Vector3f scale;

        public Part(String name, Part parent, Mesh mesh, Vector3f pivotPoint, Quaternionf rotation) {
            this.name = name;
            this.parent = parent;
            this.children = new ArrayList<Part>();
            this.mesh = mesh;

            this.dirty = true;

            this.pivotPoint = pivotPoint;
            this.rotation = rotation;
        }

        public void updateMeshTransforms(Matrix4f transformationMatrix) {
            if (!dirty) return;
            //Log.info();
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
