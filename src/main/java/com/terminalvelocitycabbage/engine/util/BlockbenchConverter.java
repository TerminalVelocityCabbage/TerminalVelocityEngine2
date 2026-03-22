package com.terminalvelocitycabbage.engine.util;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.json.JsonFormat;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.terminalvelocitycabbage.engine.filesystem.selector.FileDialogs;
import java.nio.file.Files;

import java.nio.file.Path;
import java.util.*;

public class BlockbenchConverter {

    public static void main(String[] args) {
        openAndConvert();
    }

    public static void openAndConvert() {
        Optional<Path> inputPath = FileDialogs.openFile("Select Blockbench Bedrock Geometry (.geo.json)", null, "*.geo.json");
        if (inputPath.isEmpty()) {
            System.out.println("No input file selected. Aborting.");
            return;
        }
        System.out.println("Selected input file: " + inputPath.get());

        String filename = inputPath.get().getFileName().toString();
        String modelName = filename;
        int firstDot = modelName.indexOf('.');
        if (firstDot != -1) {
            modelName = modelName.substring(0, firstDot);
        }
        
        JsonFormat jsonFormat = JsonFormat.minimalInstance();

        try (FileConfig jsonConfig = FileConfig.builder(inputPath.get(), jsonFormat).build()) {
            System.out.println("Loading config file...");
            jsonConfig.load();
            
            if (jsonConfig.isEmpty()) {
                System.out.println("Warning: Config is empty after loading. If your file is a .geo.json, try renaming it to .json so the library recognizes it, or ensure it is valid JSON.");
            }

            System.out.println("Converting to TVE Model...");
            Config tomlConfig = convertJsonToTveModel(jsonConfig, modelName);

            if (tomlConfig.isEmpty()) {
                System.out.println("Warning: Converted TOML config is empty. Check if the input JSON is a valid Bedrock geometry file.");
            }

            Path defaultSavePath = inputPath.get().getParent().resolve(modelName + ".model.toml");
            Optional<Path> outputPath = FileDialogs.saveFile("Save TVE Model (.model.toml)", defaultSavePath, "*.model.toml");
            if (outputPath.isEmpty()) {
                System.out.println("No output path selected. Aborting.");
                return;
            }
            System.out.println("Selected output file: " + outputPath.get());

            try {
                Files.createDirectories(outputPath.get().getParent());
                
                if (tomlConfig.isEmpty()) {
                    System.out.println("Warning: tomlConfig is empty, nothing to save!");
                }

                System.out.println("Saving TVE model to TOML using TomlWriter...");
                com.electronwill.nightconfig.toml.TomlWriter writer = TomlFormat.instance().createWriter();
                // metadata and cube textures should be regular tables, variant textures should be inline
                writer.setWriteTableInlinePredicate(c -> {
                    // metadata should be a regular table (section)
                    if (c.contains("model_version")) {
                        return false;
                    }
                    // Everything else (textures) can be inline
                    return true;
                });
                writer.write(tomlConfig, outputPath.get(), WritingMode.REPLACE);
                
                if (Files.exists(outputPath.get())) {
                    System.out.println("Successfully saved TVE model to: " + outputPath.get());
                    System.out.println("File size: " + Files.size(outputPath.get()) + " bytes");
                } else {
                    System.err.println("Failed to save TVE model: File does not exist after writing!");
                }
            } catch (Exception e) {
                System.err.println("Failed to save TVE model: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("An error occurred during the conversion process:");
            e.printStackTrace();
        }
    }

    static Config convertJsonToTveModel(Config json, String modelName) {
        Config.setInsertionOrderPreserved(true);
        Config toml = TomlFormat.instance().createConfig();

        List<Config> geometries = json.get("minecraft:geometry");
        if (geometries == null || geometries.isEmpty()) {
            return toml;
        }

        Config geo = geometries.get(0);
        Config description = geo.get("description");
        int width = 16;
        int height = 16;
        if (description != null) {
            Number w = description.get("texture_width");
            Number h = description.get("texture_height");
            if (w != null) width = w.intValue();
            if (h != null) height = h.intValue();
        }

        // Metadata
        toml.set("metadata.model_version", "1.0.0");
        toml.set("metadata.name", modelName);
        Config layerInfo = Config.inMemory();
        layerInfo.set("default", List.of(width, height));
        toml.set("metadata.texture_layers", List.of(layerInfo));
        toml.set("metadata.default_variant", "default");

        List<Config> bonesJson = geo.get("bones");
        Map<String, List<Double>> pivots = new HashMap<>();
        if (bonesJson != null) {
            for (Config b : bonesJson) {
                pivots.put(b.get("name"), b.get("pivot"));
            }
        }

        // Variant
        Config variant = toml.createSubConfig();
        variant.set("name", "default");
        // parent (skip if default/missing, none for default variant)
        List<String> boneNames = new ArrayList<>();
        if (bonesJson != null) {
            for (Config b : bonesJson) {
                boneNames.add(b.get("name"));
            }
        }
        variant.set("bones", boneNames);
        // exclude_bones (skip if empty)
        Config textures = variant.createSubConfig();
        textures.set("default", "MISSING");
        variant.set("textures", textures);
        toml.set("variant", List.of(variant));

        System.out.println("Processing model: " + modelName);

        // Bones, Cubes, and Anchors (locators)
        List<Config> tomlBones = new ArrayList<>();
        List<Config> tomlCubes = new ArrayList<>();
        List<Config> tomlAnchors = new ArrayList<>();

        if (bonesJson != null) {
            for (Config boneJson : bonesJson) {
                String boneName = boneJson.get("name");
                String boneParent = boneJson.get("parent");

                // Bone
                Config tomlBone = Config.inMemory();
                tomlBone.set("name", boneName);
                if (boneParent != null && !boneParent.equals("root")) {
                    tomlBone.set("parent", boneParent);
                }

                List<Number> pivot = boneJson.get("pivot");
                if (pivot == null) pivot = List.of(0, 0, 0);
                List<Number> parentPivot = boneParent != null ? (List<Number>) (Object) pivots.get(boneParent) : List.of(0, 0, 0);
                if (parentPivot == null) parentPivot = List.of(0, 0, 0);

                List<Float> position = List.of(
                        pivot.get(0).floatValue() - parentPivot.get(0).floatValue(),
                        pivot.get(1).floatValue() - parentPivot.get(1).floatValue(),
                        pivot.get(2).floatValue() - parentPivot.get(2).floatValue()
                );
                
                List<Number> rotation = boneJson.get("rotation");

                // Order for Bone: name, parent, offset, position, rotation
                // Note: current code sets offset as [0,0,0] which is default.
                // We'll only set position and rotation if they are non-default.
                // Actually, TVModelBone.of expects them if they are there, but we want to omit them if default.
                
                // offset is always 0,0,0 for bones converted from BB pivot-based bones (since pivot IS the origin)
                // Wait, BB pivot is where it rotates. In TVE, bone position is relative to parent pivot.
                
                if (!isDefaultVector(position)) {
                    tomlBone.set("position", position);
                }
                
                if (rotation != null && !isDefaultVector(rotation)) {
                    List<Float> fixedRotation = List.of(
                            -rotation.get(0).floatValue(),
                            rotation.get(1).floatValue(),
                            -rotation.get(2).floatValue()
                    );
                    tomlBone.set("rotation", fixedRotation);
                }
                
                tomlBones.add(tomlBone);

                // Cubes in this bone
                List<Config> cubesJson = boneJson.get("cubes");
                if (cubesJson != null) {
                    int cubeIndex = 0;
                    for (Config cubeJson : cubesJson) {
                        Config tomlCube = Config.inMemory();
                        tomlCube.set("name", boneName + "_cube_" + cubeIndex++);
                        tomlCube.set("parent", boneName);
                        
                        List<Number> size = cubeJson.get("size");
                        if (size == null) size = List.of(0, 0, 0);
                        List<Integer> roundedSize = new ArrayList<>(3);
                        List<Float> growAdjustment = new ArrayList<>(3);
                        for (Number n : size) {
                            float val = n.floatValue();
                            int rounded = (int) Math.ceil(val);
                            roundedSize.add(rounded);
                            growAdjustment.add((val - rounded) / 2.0f);
                        }

                        if (!isDefaultVector(roundedSize)) {
                            tomlCube.set("size", roundedSize);
                        }

                        Object inflate = cubeJson.get("inflate");
                        List<Float> grow = new ArrayList<>(3);
                        if (inflate instanceof Number) {
                            float f = ((Number) inflate).floatValue();
                            grow.add(f + growAdjustment.get(0));
                            grow.add(f + growAdjustment.get(1));
                            grow.add(f + growAdjustment.get(2));
                        } else if (inflate instanceof List) {
                            List<Number> inflateList = (List<Number>) inflate;
                            grow.add(inflateList.get(0).floatValue() + growAdjustment.get(0));
                            grow.add(inflateList.get(1).floatValue() + growAdjustment.get(1));
                            grow.add(inflateList.get(2).floatValue() + growAdjustment.get(2));
                        } else {
                            grow.addAll(growAdjustment);
                        }

                        if (!isDefaultVector(grow)) {
                            tomlCube.set("grow", grow);
                        }

                        List<Number> origin = cubeJson.get("origin");
                        if (origin == null) origin = List.of(0, 0, 0);
                        List<Number> cubePivot = cubeJson.get("pivot");
                        if (cubePivot == null) cubePivot = pivot;
                        
                        // position = cubePivot - bonePivot
                        List<Float> cubePos = List.of(
                                cubePivot.get(0).floatValue() - pivot.get(0).floatValue(),
                                cubePivot.get(1).floatValue() - pivot.get(1).floatValue(),
                                cubePivot.get(2).floatValue() - pivot.get(2).floatValue()
                        );
                        if (!isDefaultVector(cubePos)) {
                            tomlCube.set("position", cubePos);
                        }
                        
                        // offset = origin - cubePivot
                        List<Float> cubeOffset = List.of(
                                origin.get(0).floatValue() - cubePivot.get(0).floatValue() + growAdjustment.get(0),
                                origin.get(1).floatValue() - cubePivot.get(1).floatValue() + growAdjustment.get(1),
                                origin.get(2).floatValue() - cubePivot.get(2).floatValue() + growAdjustment.get(2)
                        );
                        if (!isDefaultVector(cubeOffset)) {
                            tomlCube.set("offset", cubeOffset);
                        }

                        List<Number> cubeRotation = cubeJson.get("rotation");
                        if (cubeRotation != null && !isDefaultVector(cubeRotation)) {
                            List<Float> fixedCubeRotation = List.of(
                                    -cubeRotation.get(0).floatValue(),
                                    cubeRotation.get(1).floatValue(),
                                    -cubeRotation.get(2).floatValue()
                            );
                            tomlCube.set("rotation", fixedCubeRotation);
                        }

                        // Textures
                        Config tomlTextures = tomlCube.createSubConfig();
                        tomlTextures.set("layer", "default");
                        
                        Object uvObj = cubeJson.get("uv");
                        if (uvObj instanceof List) {
                            // Box UV
                            List<Number> uv = (List<Number>) uvObj;
                            float u = uv.get(0).floatValue();
                            float v = uv.get(1).floatValue();
                            float sx = roundedSize.get(0).floatValue();
                            float sy = roundedSize.get(1).floatValue();
                            float sz = roundedSize.get(2).floatValue();
                            
                            // West (+X, px)
                            tomlTextures.set("px_uv", List.of((int)u, (int)(v + sz), (int)(u + sz), (int)(v + sz + sy)));
                            // East (-X, nx)
                            tomlTextures.set("nx_uv", List.of((int)(u + sz + sx), (int)(v + sz), (int)(u + 2*sz + sx), (int)(v + sz + sy)));
                            // Up (py)
                            tomlTextures.set("py_uv", List.of((int)(u + sz + sx), (int)(v + sz), (int)(u + sz), (int)v));
                            // Down (ny)
                            tomlTextures.set("ny_uv", List.of((int)(u + sz + sx), (int)v, (int)(u + sz + 2*sx), (int)(v + sz)));
                            // South (pz)
                            tomlTextures.set("pz_uv", List.of((int)(u + 2*sz + sx), (int)(v + sz), (int)(u + 2*sz + 2*sx), (int)(v + sz + sy)));
                            // North (nz)
                            tomlTextures.set("nz_uv", List.of((int)(u + sz), (int)(v + sz), (int)(u + sz + sx), (int)(v + sz + sy)));
                        } else if (uvObj instanceof Config) {
                            // Per-Face UV
                            Config uvMap = (Config) uvObj;
                            tomlTextures.set("px_uv", getFaceUv(uvMap, "west", true, false));
                            tomlTextures.set("nx_uv", getFaceUv(uvMap, "east", true, false));
                            tomlTextures.set("py_uv", getFaceUv(uvMap, "up", true, true));
                            tomlTextures.set("ny_uv", getFaceUv(uvMap, "down", true, true));
                            tomlTextures.set("pz_uv", getFaceUv(uvMap, "south", false, false));
                            tomlTextures.set("nz_uv", getFaceUv(uvMap, "north", false, false));
                        }
                        
                        tomlCube.set("textures", tomlTextures);
                        tomlCubes.add(tomlCube);
                    }
                }
                
                // Locators as Anchors
                Config locators = boneJson.get("locators");
                if (locators != null) {
                    for (Config.Entry entry : locators.entrySet()) {
                        Config tomlAnchor = Config.inMemory();
                        tomlAnchor.set("name", entry.getKey());
                        tomlAnchor.set("parent", boneName);
                        
                        Object locVal = entry.getValue();
                        if (locVal instanceof Config) {
                            Config locConfig = (Config) locVal;
                            List<Number> locOffset = locConfig.get("offset"); // BB offset is position?
                            if (locOffset != null && !isDefaultVector(locOffset)) {
                                List<Float> locOffsetF = List.of(locOffset.get(0).floatValue(), locOffset.get(1).floatValue(), locOffset.get(2).floatValue());
                                tomlAnchor.set("position", locOffsetF);
                            }
                            List<Number> locRot = locConfig.get("rotation");
                            if (locRot != null && !isDefaultVector(locRot)) {
                                List<Float> locRotF = List.of(-locRot.get(0).floatValue(), locRot.get(1).floatValue(), -locRot.get(2).floatValue());
                                tomlAnchor.set("rotation", locRotF);
                            }
                        } else if (locVal instanceof List) {
                            List<Number> locOffset = (List<Number>) locVal;
                            if (!isDefaultVector(locOffset)) {
                                List<Float> locOffsetF = List.of(locOffset.get(0).floatValue(), locOffset.get(1).floatValue(), locOffset.get(2).floatValue());
                                tomlAnchor.set("position", locOffsetF);
                            }
                        }
                        tomlAnchors.add(tomlAnchor);
                    }
                }
            }
        }

        toml.set("bone", tomlBones);
        toml.set("cube", tomlCubes);
        if (!tomlAnchors.isEmpty()) {
            toml.set("anchor", tomlAnchors);
        }

        System.out.println("Conversion complete: " + tomlBones.size() + " bones, " + tomlCubes.size() + " cubes, " + tomlAnchors.size() + " anchors.");

        return toml;
    }

    private static boolean isDefaultVector(List<? extends Number> vec) {
        if (vec == null) return true;
        for (Number n : vec) {
            if (n.floatValue() != 0.0f) return false;
        }
        return true;
    }

    private static List<Integer> getFaceUv(Config uvMap, String faceName, boolean flipHorizontal, boolean flipVertical) {
        Config face = uvMap.get(faceName);
        if (face == null) return List.of(0, 0, 0, 0);
        List<Number> uv = face.get("uv");
        List<Number> uvSize = face.get("uv_size");
        if (uv == null || uv.size() < 2) return List.of(0, 0, 0, 0);
        
        int u1 = uv.get(0).intValue();
        int v1 = uv.get(1).intValue();
        int u2 = u1;
        int v2 = v1;
        
        if (uvSize != null && uvSize.size() >= 2) {
            u2 += uvSize.get(0).intValue();
            v2 += uvSize.get(1).intValue();
        } else if (uv.size() >= 4) {
            u2 = uv.get(2).intValue();
            v2 = uv.get(3).intValue();
        }

        if (flipHorizontal) {
            int temp = u1;
            u1 = u2;
            u2 = temp;
        }

        if (flipVertical) {
            int temp = v1;
            v1 = v2;
            v2 = temp;
        }

        Object rotObj = face.get("rotation");
        int rotation = (rotObj instanceof Number) ? ((Number) rotObj).intValue() : 0;
        if (rotation != 0) {
            return List.of(u1, v1, u2, v2, rotation);
        }
        
        return List.of(u1, v1, u2, v2);
    }
}
