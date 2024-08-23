package com.terminalvelocitycabbage.engine.client.renderer.model;

import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.debug.Log;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Map;

public class Model {

    VertexFormat format;
    Map<String, Bone> parts;
    Mesh mesh;

    public Model(VertexFormat format, Map<String, Bone> bones, Mesh mesh) {
        this.format = format;
        this.parts = bones;
        this.parts.values().forEach(bone -> bone.model = this);
        this.mesh = mesh;
    }

    public void render() {
        mesh.render();
    }

    public void cleanup() {
        mesh.cleanup();
    }

    public VertexFormat getFormat() {
        return format;
    }

    public Bone getBone(String partName) {
        return parts.get(partName);
    }

    public Map<String, Bone> getBones() {
        return parts;
    }

    public Mesh getMesh() {
        return mesh;
    }

    public static class Bone {

        Model model;

        String name;
        String parentName;
        int boneIndex;

        boolean dirty;

        Vector3f offset;
        Quaternionf rotation;
        Vector3f scale;

        public Bone(String name, String parentName, Vector3f pivotPoint, Quaternionf rotation, Vector3f scale, int boneIndex) {
            this.name = name;
            this.parentName = parentName;

            this.dirty = true;

            this.offset = pivotPoint;
            this.rotation = rotation;
            this.scale = scale;

            this.boneIndex = boneIndex;
        }

        public String getParentName() {
            return parentName;
        }

        public String getName() {
            return name;
        }

        //TODO cache this offset so we don't query every frame, do this on model instantiation
        public Vector3f getOffset() {
            Vector3f retOffset = new Vector3f(offset);
            var parent = model.getBone(parentName);
            if (parent != null) {
                retOffset.add(parent.getOffset(), retOffset);
            }
            return retOffset;
        }

        public int getBoneIndex() {
            return boneIndex;
        }
    }

}
