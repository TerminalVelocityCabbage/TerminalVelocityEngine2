package com.terminalvelocitycabbage.engine.client.renderer.model.bedrock;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Utility class for loading Minecraft Bedrock Edition geometry and animation files.
 * Uses the `night-config` library to parse the JSON-formatted Bedrock data.
 */
public class BedrockModelLoader {

    /**
     * Helper to retrieve a float from a config, handling potential Integer types.
     */
    private static float getFloat(Config config, String path, float defaultValue) {
        Object val = config.get(path);
        if (val instanceof Number n) return n.floatValue();
        return defaultValue;
    }

    /**
     * Loads Bedrock geometry data from a resource.
     * 
     * @param resource The resource containing the `.geo.json` data
     * @param parser The parser to use (e.g., JsonFormat parser)
     * @return The parsed {@link BedrockGeometry} object
     */
    public static BedrockGeometry loadGeometry(Resource resource, ConfigParser<Config> parser) {
        Config config = parser.parse(resource.asString());

        BedrockGeometry geometry = new BedrockGeometry();
        geometry.format_version = config.get("format_version");
        List<Config> geometryList = config.get("minecraft:geometry");
        if (geometryList == null) return null;

        geometry.minecraft_geometry = new ArrayList<>();
        for (Config geoConfig : geometryList) {
            BedrockGeometry.GeometryData data = new BedrockGeometry.GeometryData();
            
            Config descConfig = geoConfig.get("description");
            data.description = new BedrockGeometry.Description();
            data.description.identifier = descConfig.get("identifier");
            data.description.texture_width = descConfig.getOrElse("texture_width", 0);
            data.description.texture_height = descConfig.getOrElse("texture_height", 0);
            data.description.visible_bounds_width = getFloat(descConfig, "visible_bounds_width", 0f);
            data.description.visible_bounds_height = getFloat(descConfig, "visible_bounds_height", 0f);
            data.description.visible_bounds_offset = descConfig.get("visible_bounds_offset");

            List<Config> bonesList = geoConfig.get("bones");
            if (bonesList != null) {
                data.bones = new ArrayList<>();
                for (Config boneConfig : bonesList) {
                    BedrockGeometry.Bone bone = new BedrockGeometry.Bone();
                    bone.name = boneConfig.get("name");
                    bone.parent = boneConfig.get("parent");
                    bone.pivot = boneConfig.get("pivot");
                    bone.rotation = boneConfig.get("rotation");
                    bone.bind_pose_rotation = boneConfig.get("bind_pose_rotation");
                    bone.mirror = boneConfig.get("mirror");
                    bone.inflate = boneConfig.get("inflate");

                    List<Config> cubesList = boneConfig.get("cubes");
                    if (cubesList != null) {
                        bone.cubes = new ArrayList<>();
                        for (Config cubeConfig : cubesList) {
                            BedrockGeometry.Cube cube = new BedrockGeometry.Cube();
                            cube.origin = cubeConfig.get("origin");
                            cube.size = cubeConfig.get("size");
                            cube.pivot = cubeConfig.get("pivot");
                            cube.rotation = cubeConfig.get("rotation");
                            cube.inflate = getFloat(cubeConfig, "inflate", 0f);
                            cube.mirror = cubeConfig.get("mirror");

                            Object uvData = cubeConfig.get("uv");
                            if (uvData instanceof Config uvConfig) {
                                cube.uv = new HashMap<>();
                                for (Config.Entry entry : uvConfig.entrySet()) {
                                    if (entry.getValue() instanceof Config faceConfig) {
                                        BedrockGeometry.UVMappedFace face = new BedrockGeometry.UVMappedFace();
                                        face.uv = faceConfig.get("uv");
                                        face.uv_size = faceConfig.get("uv_size");
                                        cube.uv.put(entry.getKey(), face);
                                    }
                                }
                            } else if (uvData instanceof List<?> uvList) {
                                cube.uv_list = (List<Float>) uvList;
                            }
                            bone.cubes.add(cube);
                        }
                    }
                    data.bones.add(bone);
                }
            }
            geometry.minecraft_geometry.add(data);
        }

        return geometry;
    }
}
