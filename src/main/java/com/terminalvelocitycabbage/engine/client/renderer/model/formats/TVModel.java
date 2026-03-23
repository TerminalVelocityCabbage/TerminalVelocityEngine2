package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.github.zafarkhaja.semver.Version;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.model.MeshTexturePair;
import com.terminalvelocitycabbage.engine.client.renderer.model.ModelConfig;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.ConfigUtils;
import com.terminalvelocitycabbage.engine.util.touples.Pair;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;

import java.util.*;

public record TVModel(
        TVModelMetadata metadata,
        Map<String, TVModelVariant> variants,
        Map<String, TVModelBone> bones,
        Map<String, Integer> boneIndices,
        Map<String, TVModelCube> cubes,
        Map<String, TVModelAnchor> anchors
) {

    public static ModelConfig configOf(String namespace, TVModel model, String variantName) {
        TVModelVariant variant = model.variants().get(variantName);
        if (variant == null) {
            Log.error("Variant not found: " + variantName);
            return null;
        }

        Set<String> textureLayers = model.metadata().textureLayers().keySet();
        List<MeshTexturePair> pairs = new ArrayList<>();

        for (String layer : textureLayers) {
            Identifier textureId = findTextureForLayer(model, variant, layer);
            if (textureId == null) {
                Log.error("Texture not found for layer " + layer + " in variant " + variantName);
                continue;
            }

            Identifier meshId = new Identifier(namespace, "mesh", model.metadata().name() + "_" + layer);
            pairs.add(new MeshTexturePair(meshId, textureId));
        }

        return new ModelConfig(pairs);
    }

    private static Identifier findTextureForLayer(TVModel model, TVModelVariant variant, String layer) {
        if (variant.textures().containsKey(layer)) {
            return variant.textures().get(layer);
        }
        if (variant.parent() != null && !variant.parent().isEmpty()) {
            TVModelVariant parent = model.variants().get(variant.parent());
            if (parent != null) {
                return findTextureForLayer(model, parent, layer);
            }
        }
        TVModelVariant defaultVariant = model.metadata().defaultVariant();
        if (variant != defaultVariant && defaultVariant != null) {
            if (defaultVariant.textures().containsKey(layer)) {
                return defaultVariant.textures().get(layer);
            }
        }
        return null;
    }
    
    public static TVModel of(Identifier modelResource) {

        // Resource
        Resource resource = ClientBase.getInstance().getFileSystem().getResource(ResourceCategory.MODEL, modelResource);
        if (resource == null) {
            Log.crash("Could not find model resource: " + modelResource);
            return null;
        }
        Config config = TomlFormat.instance().createParser().parse(resource.asString());

        // Variants
        Map<String, TVModelVariant> variants = new HashMap<>();
        List<Config> variantsList = config.get("variant");
        if (variantsList == null) throw new IllegalArgumentException("Model does not contain any variants, at least one is required.");
        for (Config variantConfig : variantsList) {
            TVModelVariant variant = TVModelVariant.of(variantConfig);
            variants.put(variant.name(), variant);
        }

        // Metadata
        TVModelMetadata metadata = TVModelMetadata.of(config.get("metadata"), variants);

        // Bones
        Map<String, TVModelBone> bones = new LinkedHashMap<>();
        Map<String, Integer> boneIndices = new HashMap<>();
        List<Config> bonesList = config.get("bone");
        if (bonesList != null && !bonesList.isEmpty()) {
            for (Config boneConfig : bonesList) {
                var bone = TVModelBone.of(boneConfig);
                bones.put(bone.name(), bone);
            }
            int i = 0;
            for (String boneName : bones.keySet()) {
                boneIndices.put(boneName, i++);
            }
        }

        // Cubes
        Map<String, TVModelCube> cubes = new HashMap<>();
        List<Config> cubesList = config.get("cube");
        if (cubesList == null) throw new IllegalArgumentException("Model does not contain any cubes, at least one is required.");
        for (Config cubeConfig : cubesList) {
            var cube = TVModelCube.of(cubeConfig);
            cubes.put(cube.name(), cube);
        }


        // Anchors
        Map<String, TVModelAnchor> anchors = new HashMap<>();
        List<Config> anchorsList = config.get("anchor");
        if (anchorsList != null && !anchorsList.isEmpty()) {
            for (Config anchorConfig : anchorsList) {
                var anchor = TVModelAnchor.of(anchorConfig);
                anchors.put(anchor.name(), anchor);
            }
        }

        return new TVModel(metadata, variants, bones, boneIndices, cubes, anchors);
    }

    public record TVModelMetadata(
            Version version,
            String name,
            Map<String, Vector2i> textureLayers,
            TVModelVariant defaultVariant
    ) {

        public static TVModelMetadata of(Config metadataConfig, Map<String, TVModelVariant> variants) {
            Version version = Version.valueOf(metadataConfig.get("model_version"));
            String name = metadataConfig.get("name");
            List<Config> textureLayersConfigs = metadataConfig.get("texture_layers");
            Map<String, Vector2i> textureLayers = new LinkedHashMap<>();
            if (textureLayersConfigs != null) {
                for (Config layerConfig : textureLayersConfigs) {
                    for (Config.Entry entry : layerConfig.entrySet()) {
                        textureLayers.put(entry.getKey(), ConfigUtils.parseVector2i((List<? extends Number>) entry.getValue()));
                    }
                }
            }
            String defaultVariantName = metadataConfig.get("default_variant");
            return new TVModelMetadata(version, name, textureLayers, variants.get(defaultVariantName));
        }

    }

    public record TVModelVariant(
            String name,
            String parent,
            List<String> bones,
            List<String> excludedBones,
            Map<String, Identifier> textures //Layer -> texture
    ) {

        public static TVModelVariant of(Config variantConfig) {
            String vName = variantConfig.get("name");
            String parent = variantConfig.get("parent");
            List<String> bones = variantConfig.contains("bones") ? variantConfig.get("bones") : List.of();
            List<String> excludedBones = variantConfig.contains("exclude_bones") ? variantConfig.get("exclude_bones") : List.of();
            Config texturesConfig = variantConfig.get("textures");
            Map<String, Identifier> textures = new HashMap<>();
            if (texturesConfig != null) {
                for (Config.Entry entry : texturesConfig.entrySet()) {
                    textures.put(entry.getKey(), Identifier.fromString(entry.getValue(), "texture"));
                }
            }
            return new TVModelVariant(vName, parent, bones, excludedBones, textures);
        }

    }

    public record TVModelBone(
            String name,
            Optional<String> parent,
            Vector3f position,
            Vector3f offset,
            Vector3f rotation
    ) {

        public static TVModelBone of(Config boneConfig) {
            String bName = boneConfig.get("name");
            String bParent = boneConfig.get("parent");
            Vector3f position = ConfigUtils.parseVector3f(boneConfig.get("position"));
            Vector3f offset = ConfigUtils.parseVector3f(boneConfig.get("offset"));
            Vector3f rotation = ConfigUtils.parseVector3f(boneConfig.get("rotation"));
            return new TVModelBone(bName, Optional.ofNullable(bParent), position, offset, rotation);
        }

    }

    public record TVModelCube(
            String name, //The name of this cube
            Optional<String> parent, //The name of the parent cube or bone, if any.
            Vector3i size,
            Vector3f grow,
            Vector3f position,
            Vector3f offset,
            Vector3f rotation,
            TVModelCubeTextureMapping textures
    ) {

        public static TVModelCube of(Config cubeConfig) {
            String cName = cubeConfig.get("name");
            String cParent = cubeConfig.get("parent");
            Vector3i size = ConfigUtils.parseVector3i(cubeConfig.get("size"));
            Vector3f grow = ConfigUtils.parseVector3f(cubeConfig.get("grow"));
            Vector3f position = ConfigUtils.parseVector3f(cubeConfig.get("position"));
            Vector3f offset = ConfigUtils.parseVector3f(cubeConfig.get("offset"));
            Vector3f rotation = ConfigUtils.parseVector3f(cubeConfig.get("rotation"));
            return new TVModelCube(
                    cName,
                    Optional.ofNullable(cParent),
                    size,
                    grow,
                    position,
                    offset,
                    rotation,
                    TVModelCubeTextureMapping.of(cubeConfig.get("textures")));
        }

    }

    public record TVModelCubeTextureMapping(
            String layer,
            Optional<TVModelFaceUV> pxFace, //Positive X Face (right/east)
            Optional<TVModelFaceUV> nxFace, //Negative X Face (left/west)
            Optional<TVModelFaceUV> pyFace, //Positive Y Face (up/top)
            Optional<TVModelFaceUV> nyFace, //Negative Y Face (down/bottom)
            Optional<TVModelFaceUV> pzFace, //Positive Z Face (forward/north)
            Optional<TVModelFaceUV> nzFace  //Negative Z Face (back/south)
    ) {

        public record TVModelFaceUV(Vector2i u1v1, Vector2i u2v2, int rotation) {}

        private static TVModelCubeTextureMapping of(Config texturesConfig) {
            String layer = texturesConfig.get("layer");
            return new TVModelCubeTextureMapping(
                    layer,
                    parseFace(texturesConfig, "px_uv"),
                    parseFace(texturesConfig, "nx_uv"),
                    parseFace(texturesConfig, "py_uv"),
                    parseFace(texturesConfig, "ny_uv"),
                    parseFace(texturesConfig, "pz_uv"),
                    parseFace(texturesConfig, "nz_uv")
            );
        }

        private static Optional<TVModelFaceUV> parseFace(Config config, String key) {
            List<? extends Number> uv = config.get(key);
            if (uv == null || uv.size() < 4) throw new IllegalArgumentException("Invalid UV coordinates for face: " + key + " UVs are expected to be in the format of a 4 integer array [ux, uy, vx, vy]");
            int rotation = uv.size() >= 5 ? uv.get(4).intValue() : 0;
            return Optional.of(new TVModelFaceUV(new Vector2i(uv.get(0).intValue(), uv.get(1).intValue()), new Vector2i(uv.get(2).intValue(), uv.get(3).intValue()), rotation));
        }

    }

    public record TVModelAnchor(
            String name,
            Optional<String> parent,
            Vector3f position,
            Vector3f offset,
            Vector3f rotation
    ) {

        public static TVModelAnchor of(Config anchorConfig) {
            String aName = anchorConfig.get("name");
            String aParent = anchorConfig.get("parent");
            Vector3f position = ConfigUtils.parseVector3f(anchorConfig.get("position"));
            Vector3f offset = ConfigUtils.parseVector3f(anchorConfig.get("offset"));
            Vector3f rotation = ConfigUtils.parseVector3f(anchorConfig.get("rotation"));
            return new TVModelAnchor(aName, Optional.ofNullable(aParent), position, offset, rotation);
        }

    }

}
