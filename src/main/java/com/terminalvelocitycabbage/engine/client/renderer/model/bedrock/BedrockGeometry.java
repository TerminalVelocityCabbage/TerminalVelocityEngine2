package com.terminalvelocitycabbage.engine.client.renderer.model.bedrock;

import java.util.List;
import java.util.Map;

public class BedrockGeometry {
    public String format_version;
    public List<GeometryData> minecraft_geometry;

    public static class GeometryData {
        public Description description;
        public List<Bone> bones;
    }

    public static class Description {
        public String identifier;
        public int texture_width;
        public int texture_height;
        public double visible_bounds_width;
        public double visible_bounds_height;
        public List<Float> visible_bounds_offset;
    }

    public static class Bone {
        public String name;
        public String parent;
        public List<Float> pivot;
        public List<Float> rotation;
        public List<Float> bind_pose_rotation;
        public List<Cube> cubes;
        public Boolean mirror;
        public Float inflate;
    }

    public static class Cube {
        public List<Float> origin;
        public List<Float> size;
        public List<Float> pivot;
        public List<Float> rotation;
        public Map<String, UVMappedFace> uv;
        public List<Float> uv_list;
        public float inflate;
        public Boolean mirror;
    }

    public static class UVMappedFace {
        public List<Float> uv;
        public List<Float> uv_size;
    }
}
