package com.terminalvelocitycabbage.engine.client.renderer.model.bedrock;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.json.JsonFormat;
import com.github.zafarkhaja.semver.Version;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.util.ConfigUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BedrockModel {

    Version formatVersion;
    BedrockGeometryDescription geometryDescription;
    BedrockBone[] bones;

    private BedrockModel(Version formatVersion, BedrockGeometryDescription geometryDescription, BedrockBone[] bones) {
        this.formatVersion = formatVersion;
        this.geometryDescription = geometryDescription;
        this.bones = bones;
    }

    public void print() {
        Log.info(formatVersion.toString());
        Log.info(geometryDescription.toString());
        for (BedrockBone bone : bones) {
            Log.info(bone.toString());
        }
    }

    private static class BedrockCube {

        float[] origin;
        int[] size;
        int[] uv;

        public BedrockCube(float[] origin, int[] size, int[] uv) {
            this.origin = origin;
            this.size = size;
            this.uv = uv;
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
        BedrockCube[] cubes;

        public BedrockBone(String name, String parent, float[] pivot, float[] rotation, BedrockCube[] cubes) {
            this.name = name;
            this.parent = parent;
            this.pivot = pivot;
            this.rotation = rotation;
            this.cubes = cubes;
        }

        @Override
        public String toString() {
            return "BedrockBone{" +
                    "name='" + name + '\'' +
                    ", parent='" + parent + '\'' +
                    ", pivot=" + Arrays.toString(pivot) +
                    ", rotation=" + Arrays.toString(rotation) +
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

        public static BedrockModel loadModel(Resource modelResource) {

            String resourceString = modelResource.asString();

            ConfigFormat<?> jsonFormat = JsonFormat.fancyInstance();
            ConfigParser<?> parser = jsonFormat.createParser();
            Config config = parser.parse(resourceString);

            formatVersion = Version.parse(config.get("format_version"));

            List<Config> subConfig = config.get("minecraft:geometry");
            Config geometryConfig = subConfig.get(0);

            geometryDescription = parseGeometryDescription(geometryConfig);
            bones = parseBones(geometryConfig);

            return new BedrockModel(formatVersion, geometryDescription, bones);
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

            return new BedrockBone(name, parent, pivot, rotation, parseCubes(config));
        }

        private static BedrockCube[] parseCubes(Config config) {

            List<Config> cubes = config.get("cubes");
            List<BedrockCube> cubesList = new ArrayList<>();

            cubes.forEach(cube -> cubesList.add(parseCube(cube)));

            return cubesList.toArray(new BedrockCube[0]);
        }

        private static BedrockCube parseCube(Config cube) {

            float[] origin = ConfigUtils.numberListToFloatArray(cube.get("origin"));
            int[] size = ConfigUtils.numberListToIntArray(cube.get("size"));
            int[] uv = ConfigUtils.numberListToIntArray(cube.get("uv"));

            return new BedrockCube(origin, size, uv);
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

}
