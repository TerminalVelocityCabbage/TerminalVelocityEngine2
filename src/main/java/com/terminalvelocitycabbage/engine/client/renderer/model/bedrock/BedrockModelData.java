package com.terminalvelocitycabbage.engine.client.renderer.model.bedrock;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.json.JsonFormat;
import com.github.zafarkhaja.semver.Version;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexAttribute;
import com.terminalvelocitycabbage.engine.client.renderer.elements.VertexFormat;
import com.terminalvelocitycabbage.engine.client.renderer.model.Mesh;
import com.terminalvelocitycabbage.engine.client.renderer.model.Model;
import com.terminalvelocitycabbage.engine.client.renderer.model.Vertex;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.util.ConfigUtils;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.*;

public class BedrockModelData {

    public static final VertexFormat BEDROCK_VERTEX_FORMAT = VertexFormat
            .builder()
            .addElement(VertexAttribute.XYZ_POSITION)
            .addElement(VertexAttribute.XYZ_NORMAL)
            .addElement(VertexAttribute.UV)
            .addElement(VertexAttribute.BONE_INDEX)
            .build();

    Version formatVersion;
    BedrockGeometryDescription geometryDescription;
    BedrockBone[] bones;

    private BedrockModelData(Version formatVersion, BedrockGeometryDescription geometryDescription, BedrockBone[] bones) {
        this.formatVersion = formatVersion;
        this.geometryDescription = geometryDescription;
        this.bones = bones;
    }

    private static class BedrockCube {

        float[] origin;
        int[] size;
        int[] uv;
        float[] pivot;
        float[] rotation;
        float[] scale;

        public BedrockCube(float[] origin, int[] size, int[] uv, float[] pivot, float[] rotation, float[] scale) {
            this.origin = origin;
            this.size = size;
            this.uv = uv;
            this.pivot = pivot;
            this.rotation = rotation;
            this.scale = scale;
        }

        /**
         *
         * Positions are defined by an origin and a size, there will be 3 Vertexes at each position, only difference
         * Will be the UV.
         *
         * Origin is always the smallest of the 3 components, so it will always be at the SWBottom corner, meaning that
         * the position components of the other 7 vertexes will be either the same as the origin or more North East or Up
         *
         * X is E/W (E is +), Y is T/B (T is +), Z is N/S (S is +)
         *
         *     "* <-- Origin: (U,V)" the * is the origin, since OpenGL does UV from TL to BR this should already be right
         *
         *     Offsets from the origin are defined by the size array 123 + xyz, offsets shown below:
         *     |   s2   |   s0  |   s2  |   s0   |
         *     *        1_______2_______3           ---
         *              |       |BR   BL|
         *              |   T   |   B   |           s2
         *              |       |TR   TL|7
         *      4_______5_______6_______8_______9   ---
         *      |       |       |       |       |
         *      |   E   |   N   |   W   |   S   |   s1
         *      |       |       |       |       |
         *      10-----11------12------13------14   ---
         *      For UVs all except Bottom use top/bottom/left/right based on above
         *      only 13 UV positions matter, numbered above
         *
         * The UVs need to be divided by the texture size to get them from 0 to 1
         *
         * Naming conventions for the variables in this method:
         * A vertex will be named based on the cardinal directions plus up/down
         * nsew for cardinal directions u for up and d for down.
         *
         * Vertex net will be the vertex to the North East Top corner of this cuboid
         *
         * @return A mesh represented from this cube
         */
        public Mesh toMesh(int textureWidth, int textureHeight, int boneIndex) {

            //Vertex positions
            //TODO start all at the origin, rotate them, then move them to their size to apply rotations to vertex positions
            Vector3f netPos = new Vector3f(origin[0] + size[0],  origin[1] + size[1],   origin[2] + 0f);
            Vector3f nebPos = new Vector3f(origin[0] + size[0],  origin[1] + 0f,        origin[2] + 0f);
            Vector3f nwtPos = new Vector3f(origin[0] + 0f,       origin[1] + size[1],   origin[2] + 0f);
            Vector3f nwbPos = new Vector3f(origin[0] + 0f,       origin[1] + 0f,        origin[2] + 0f);
            Vector3f setPos = new Vector3f(origin[0] + size[0],  origin[1] + size[1],   origin[2] + size[2]);
            Vector3f sebPos = new Vector3f(origin[0] + size[0],  origin[1] + 0f,        origin[2] + size[2]);
            Vector3f swtPos = new Vector3f(origin[0] + 0f,       origin[1] + size[1],   origin[2] + size[2]);
            Vector3f swbPos = new Vector3f(origin[0] + 0f,       origin[1] + 0f,        origin[2] + size[2]);

            if (rotation.length > 0 && pivot.length > 0) {

                Quaternionf q = new Quaternionf();
                q.rotateXYZ((float) Math.toRadians(rotation[0]), (float) Math.toRadians(rotation[1]), (float) Math.toRadians(rotation[2]));

                netPos.sub(pivot[0], pivot[1], pivot[2]);
                nebPos.sub(pivot[0], pivot[1], pivot[2]);
                nwtPos.sub(pivot[0], pivot[1], pivot[2]);
                nwbPos.sub(pivot[0], pivot[1], pivot[2]);
                setPos.sub(pivot[0], pivot[1], pivot[2]);
                sebPos.sub(pivot[0], pivot[1], pivot[2]);
                swtPos.sub(pivot[0], pivot[1], pivot[2]);
                swbPos.sub(pivot[0], pivot[1], pivot[2]);

                netPos.rotate(q);
                nebPos.rotate(q);
                nwtPos.rotate(q);
                nwbPos.rotate(q);
                setPos.rotate(q);
                sebPos.rotate(q);
                swtPos.rotate(q);
                swbPos.rotate(q);

                netPos.add(pivot[0], pivot[1], pivot[2]);
                nebPos.add(pivot[0], pivot[1], pivot[2]);
                nwtPos.add(pivot[0], pivot[1], pivot[2]);
                nwbPos.add(pivot[0], pivot[1], pivot[2]);
                setPos.add(pivot[0], pivot[1], pivot[2]);
                sebPos.add(pivot[0], pivot[1], pivot[2]);
                swtPos.add(pivot[0], pivot[1], pivot[2]);
                swbPos.add(pivot[0], pivot[1], pivot[2]);
            }

            //Face normals
            Vector3f northNormal = new Vector3f(0, 0, -1);
            Vector3f eastNormal = new Vector3f(1, 0, 0);
            Vector3f southNormal = new Vector3f(0, 0, 1);
            Vector3f westNormal = new Vector3f(-1, 0, 0);
            Vector3f upNormal = new Vector3f(0, 1, 0);
            Vector3f downNormal = new Vector3f(0, -1, 0);

            //UVs
            Vector2f uv1 = new Vector2f(uv[0] + size[2],                                uv[1]).div(textureWidth, textureHeight);
            Vector2f uv2 = new Vector2f(uv[0] + size[2] + size[0],                      uv[1]).div(textureWidth, textureHeight);
            Vector2f uv3 = new Vector2f(uv[0] + size[2] + size[0] + size[0],            uv[1] + size[2]).div(textureWidth, textureHeight);
            Vector2f uv4 = new Vector2f(uv[0],                                             uv[1] + size[2]).div(textureWidth, textureHeight);
            Vector2f uv5 = new Vector2f(uv[0] + size[2],                                uv[1] + size[2]).div(textureWidth, textureHeight);
            Vector2f uv6 = new Vector2f(uv[0] + size[2] + size[0],                      uv[1] + size[2]).div(textureWidth, textureHeight);
            Vector2f uv7 = new Vector2f(uv[0] + size[2] + size[0] + size[0],            uv[1] + size[2]).div(textureWidth, textureHeight);
            Vector2f uv8 = new Vector2f(uv[0] + size[2] + size[0] + size[2],            uv[1] + size[2]).div(textureWidth, textureHeight);
            Vector2f uv9 = new Vector2f(uv[0] + size[2] + size[0] + size[2] + size[0],  uv[1] + size[2]).div(textureWidth, textureHeight);
            Vector2f uv10 = new Vector2f(uv[0],                                            uv[1] + size[2] + size[1]).div(textureWidth, textureHeight);
            Vector2f uv11 = new Vector2f(uv[0] + size[2],                               uv[1] + size[2] + size[1]).div(textureWidth, textureHeight);
            Vector2f uv12 = new Vector2f(uv[0] + size[2] + size[0],                     uv[1] + size[2] + size[1]).div(textureWidth, textureHeight);
            Vector2f uv13 = new Vector2f(uv[0] + size[2] + size[0] + size[2],           uv[1] + size[2] + size[1]).div(textureWidth, textureHeight);
            Vector2f uv14 = new Vector2f(uv[0] + size[2] + size[0] + size[2] + size[0], uv[1] + size[2] + size[1]).div(textureWidth, textureHeight);

            //North Face
            Vertex northTL = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(nwtPos).setXYZNormal(northNormal).setUV(uv6).setBoneIndex(boneIndex);
            Vertex northTR = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(netPos).setXYZNormal(northNormal).setUV(uv5).setBoneIndex(boneIndex);
            Vertex northBL = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(nwbPos).setXYZNormal(northNormal).setUV(uv12).setBoneIndex(boneIndex);
            Vertex northBR = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(nebPos).setXYZNormal(northNormal).setUV(uv11).setBoneIndex(boneIndex);
            Vertex eastTL = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(netPos).setXYZNormal(eastNormal).setUV(uv5).setBoneIndex(boneIndex);
            Vertex eastTR = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(setPos).setXYZNormal(eastNormal).setUV(uv4).setBoneIndex(boneIndex);
            Vertex eastBL = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(nebPos).setXYZNormal(eastNormal).setUV(uv11).setBoneIndex(boneIndex);
            Vertex eastBR = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(sebPos).setXYZNormal(eastNormal).setUV(uv10).setBoneIndex(boneIndex);
            Vertex southTL = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(setPos).setXYZNormal(southNormal).setUV(uv9).setBoneIndex(boneIndex);
            Vertex southTR = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(swtPos).setXYZNormal(southNormal).setUV(uv8).setBoneIndex(boneIndex);
            Vertex southBL = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(sebPos).setXYZNormal(southNormal).setUV(uv14).setBoneIndex(boneIndex);
            Vertex southBR = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(swbPos).setXYZNormal(southNormal).setUV(uv13).setBoneIndex(boneIndex);
            Vertex westTL = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(swtPos).setXYZNormal(westNormal).setUV(uv8).setBoneIndex(boneIndex);
            Vertex westTR = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(nwtPos).setXYZNormal(westNormal).setUV(uv6).setBoneIndex(boneIndex);
            Vertex westBL = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(swbPos).setXYZNormal(westNormal).setUV(uv13).setBoneIndex(boneIndex);
            Vertex westBR = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(nwbPos).setXYZNormal(westNormal).setUV(uv12).setBoneIndex(boneIndex);
            Vertex topTL = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(swtPos).setXYZNormal(upNormal).setUV(uv2).setBoneIndex(boneIndex);
            Vertex topTR = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(setPos).setXYZNormal(upNormal).setUV(uv1).setBoneIndex(boneIndex);
            Vertex topBL = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(nwtPos).setXYZNormal(upNormal).setUV(uv6).setBoneIndex(boneIndex);
            Vertex topBR = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(netPos).setXYZNormal(upNormal).setUV(uv5).setBoneIndex(boneIndex);
            Vertex bottomTL = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(nebPos).setXYZNormal(downNormal).setUV(uv7).setBoneIndex(boneIndex);
            Vertex bottomTR = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(nwbPos).setXYZNormal(downNormal).setUV(uv6).setBoneIndex(boneIndex);
            Vertex bottomBL = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(sebPos).setXYZNormal(downNormal).setUV(uv3).setBoneIndex(boneIndex);
            Vertex bottomBR = new Vertex(BEDROCK_VERTEX_FORMAT).setXYZPosition(swbPos).setXYZNormal(downNormal).setUV(uv2).setBoneIndex(boneIndex);

            // 0 1 2 3
            // 4 5 6 7
            // 8 9 10 11
            // 12 13 14 15
            // 16 17 18 19
            // 20 21 22 23

            Vertex[] vertices = new Vertex[]{
                    northTL, northTR, northBL, northBR,
                    eastTL, eastTR, eastBL, eastBR,
                    southTL, southTR, southBL, southBR,
                    westTL, westTR, westBL, westBR,
                    topTL, topTR, topBL, topBR,
                    bottomTL, bottomTR, bottomBL, bottomBR
            };

            //All should wind in the TL BL BR, TL, BR, TR order each face
            //Bottom should be in BR, TR, TL, BR, TL, BL
            int[] indexes = new int[]{
                    0, 2, 3, 0, 3, 1,
                    4, 6, 7, 4, 7, 5,
                    8, 10, 11, 8, 11, 9,
                    12, 14, 15, 12, 15, 13,
                    16, 18, 19, 16, 19, 17,
                    23, 21, 20, 23, 20, 22,
            };

            return new Mesh(BEDROCK_VERTEX_FORMAT, vertices, indexes);
        }

        @Override
        public String toString() {
            return "BedrockCube{" +
                    "origin=" + Arrays.toString(origin) +
                    ", size=" + Arrays.toString(size) +
                    ", uv=" + Arrays.toString(uv) +
                    '}';
        }
    }

    private static class BedrockBone {

        String name;
        String parent;
        float[] pivot;
        float[] rotation;
        float[] scale;
        BedrockCube[] cubes;

        public BedrockBone(String name, String parent, float[] pivot, float[] rotation, float[] scale, BedrockCube[] cubes) {
            this.name = name;
            this.parent = parent;
            this.pivot = pivot;
            this.rotation = rotation;
            this.scale = scale;
            this.cubes = cubes;
        }

        @Override
        public String toString() {
            return "BedrockBone{" +
                    "name='" + name + '\'' +
                    ", parent='" + parent + '\'' +
                    ", pivot=" + Arrays.toString(pivot) +
                    ", rotation=" + Arrays.toString(rotation) +
                    ", scale=" + Arrays.toString(scale) +
                    ", cubes=" + Arrays.toString(cubes) +
                    '}';
        }
    }

    private static class BedrockGeometryDescription {

        String identifier;
        int textureWidth;
        int textureHeight;
        float visibleBoundsWidth;
        float visibleBoundsHeight;
        float[] visibleBoundsOffset;

        public BedrockGeometryDescription(String identifier, int textureWidth, int textureHeight, float visibleBoundsWidth, float visibleBoundsHeight, float[] visibleBoundsOffset) {
            this.identifier = identifier;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.visibleBoundsWidth = visibleBoundsWidth;
            this.visibleBoundsHeight = visibleBoundsHeight;
            this.visibleBoundsOffset = visibleBoundsOffset;
        }

        @Override
        public String toString() {
            return "BedrockGeometryDescription{" +
                    "identifier='" + identifier + '\'' +
                    ", textureWidth=" + textureWidth +
                    ", textureHeight=" + textureHeight +
                    ", visibleBoundsWidth=" + visibleBoundsWidth +
                    ", visibleBoundsHeight=" + visibleBoundsHeight +
                    ", visibleBoundsOffset=" + Arrays.toString(visibleBoundsOffset) +
                    '}';
        }
    }

    public static class Loader {

        static Version formatVersion;
        static BedrockGeometryDescription geometryDescription;
        static BedrockBone[] bones;

        public static BedrockModelData loadModel(Resource modelResource) {

            String resourceString = modelResource.asString();

            ConfigFormat<?> jsonFormat = JsonFormat.fancyInstance();
            ConfigParser<?> parser = jsonFormat.createParser();
            Config config = parser.parse(resourceString);

            formatVersion = Version.parse(config.get("format_version"));

            List<Config> subConfig = config.get("minecraft:geometry");
            Config geometryConfig = subConfig.get(0);

            geometryDescription = parseGeometryDescription(geometryConfig);
            bones = parseBones(geometryConfig);

            return new BedrockModelData(formatVersion, geometryDescription, bones);
        }

        private static BedrockBone[] parseBones(Config config) {

            List<Config> boneConfigs = config.get("bones");
            List<BedrockBone> bones = new ArrayList<>();

            boneConfigs.forEach(bone -> bones.add(parseBone(bone)));

            return bones.toArray(new BedrockBone[0]);
        }

        private static BedrockBone parseBone(Config config) {

            String name = config.get("name");
            String parent = config.getOrElse("parent", "none");
            float[] pivot = ConfigUtils.numberListToFloatArray(config.get("pivot"));
            float[] rotation = ConfigUtils.numberListToFloatArray(config.get("rotation"));
            float[] scale = ConfigUtils.numberListToFloatArray(config.get("scale"));

            return new BedrockBone(name, parent, pivot, rotation, scale, parseCubes(config));
        }

        private static BedrockCube[] parseCubes(Config config) {

            List<Config> cubes = config.get("cubes");
            List<BedrockCube> cubesList = new ArrayList<>();

            if (cubes != null) cubes.forEach(cube -> cubesList.add(parseCube(cube)));

            return cubesList.toArray(new BedrockCube[0]);
        }

        private static BedrockCube parseCube(Config cube) {

            float[] origin = ConfigUtils.numberListToFloatArray(cube.get("origin"));
            int[] size = ConfigUtils.numberListToIntArray(cube.get("size"));
            int[] uv = ConfigUtils.numberListToIntArray(cube.get("uv"));
            float[] pivot = ConfigUtils.numberListToFloatArray(cube.get("pivot"));
            float[] rotation = ConfigUtils.numberListToFloatArray(cube.get("rotation"));
            //TODO verify inflate
            float[] scale = ConfigUtils.numberListToFloatArray(cube.get("inflate"));

            return new BedrockCube(origin, size, uv, pivot, rotation, scale);
        }

        private static BedrockGeometryDescription parseGeometryDescription(Config config) {

            String identifier = config.get("description.identifier");
            int textureWidth = config.getInt("description.texture_width");
            int textureHeight = config.getInt("description.texture_height");
            float visibleBoundsWidth = ((Number) config.get("description.visible_bounds_width")).floatValue();
            float visibleBoundsHeight = ((Number) config.get("description.visible_bounds_height")).floatValue();
            float[] visibleBoundsOffset = ConfigUtils.numberListToFloatArray(config.get("description.visible_bounds_offset"));

            return new BedrockGeometryDescription(identifier, textureWidth, textureHeight, visibleBoundsWidth, visibleBoundsHeight, visibleBoundsOffset);
        }

    }

    public Model toModel() {

        //The information needed to create a model part extracted from all bones
        List<Mesh> meshesToCompile = new ArrayList<>();
        Map<String, Model.Bone> modelBones = new HashMap<>();

        //Loop through all bones to get staging data for a model part
        for (int i = 0; i < bones.length; i++) {
            //This iteration's bone
            BedrockBone bone = bones[i];

            //Get all the meshes from these bones and add them to a list for later compilation
            for (BedrockCube cube : bone.cubes) {
                //Convert cubes to meshes
                meshesToCompile.add(cube.toMesh(geometryDescription.textureWidth, geometryDescription.textureHeight, i));
            }

            //Default Values
            var partPivot = new Vector3f();
            var partRotation = new Quaternionf();
            var partScale = new Vector3f(1);
            //Fill values if they exist
            if (bone.pivot.length > 0) partPivot.set(bone.pivot[0], bone.pivot[1], bone.pivot[2]);
            if (bone.rotation.length > 0) partRotation.rotateXYZ(bone.rotation[0], bone.rotation[1], bone.rotation[2]);
            if (bone.scale.length > 0) partScale.set(bone.scale[0], bone.scale[1], bone.scale[2]);
            //Create the part and add it to the list of parts
            var newPart = new Model.Bone(bone.name, bone.parent, partPivot, partRotation, partScale, i);
            modelBones.put(newPart.getName(), newPart);
        }

        //Create a model from all the parts and meshes
        return new Model(BEDROCK_VERTEX_FORMAT, modelBones, Mesh.of(meshesToCompile));
    }

    public void print() {
        Log.info(formatVersion.toString());
        Log.info(geometryDescription.toString());
        for (BedrockBone bone : bones) {
            Log.info(bone.toString());
        }
    }

}
