package com.terminalvelocitycabbage.engine.util.blockbench;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.json.JsonFormat;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlWriter;
import com.terminalvelocitycabbage.engine.filesystem.selector.FileDialogs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class BlockbenchAnimationConverter {

    private static final double EPSILON = 0.0001;

    public static void main(String[] args) {
        openAndConvert();
    }

    public static void openAndConvert() {
        Optional<Path> inputPath = FileDialogs.openFile("Select Blockbench Bedrock Animation (.animation.json)", null, "*.animation.json");
        if (inputPath.isEmpty()) {
            System.out.println("No input file selected. Aborting.");
            return;
        }

        JsonFormat jsonFormat = JsonFormat.minimalInstance();
        try (FileConfig jsonConfig = FileConfig.builder(inputPath.get(), jsonFormat).build()) {
            jsonConfig.load();

            if (jsonConfig.isEmpty()) {
                System.out.println("Warning: Config is empty after loading.");
                return;
            }

            Config animations = jsonConfig.get("animations");
            if (animations == null || animations.isEmpty()) {
                System.out.println("No animations found in the file.");
                return;
            }

            Optional<Path> outputDirPath = FileDialogs.selectFolder("Select path to dump .animation.toml files", inputPath.get().getParent());
            if (outputDirPath.isEmpty()) {
                System.out.println("No output path selected. Aborting.");
                return;
            }

            String baseName = inputPath.get().getFileName().toString();
            int lastDot = baseName.lastIndexOf('.');
            if (lastDot != -1) baseName = baseName.substring(0, lastDot);
            if (baseName.endsWith(".animation")) baseName = baseName.substring(0, baseName.length() - ".animation".length());

            Path finalOutputDir = outputDirPath.get().resolve(baseName + " converted files");
            Files.createDirectories(finalOutputDir);
            System.out.println("Exporting animations to: " + finalOutputDir);

            for (Config.Entry entry : animations.entrySet()) {
                String animationName = entry.getKey();
                if (!(entry.getValue() instanceof Config animConfig)) continue;
                String fileName = animationName;
                if (fileName.startsWith("animation.")) {
                    fileName = fileName.substring("animation.".length());
                }

                System.out.println("Converting animation: " + animationName);
                Config tveAnimation = convertToTveAnimation(animationName, animConfig);
                Path outputPath = finalOutputDir.resolve(fileName + ".animation.toml");
                saveTveAnimation(tveAnimation, outputPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Config convertToTveAnimation(String name, Config bbAnim) {
        Config.setInsertionOrderPreserved(true);
        Config toml = TomlFormat.instance().createConfig();

        // Metadata
        toml.set("metadata.version", "1.0.0");
        String displayName = name;
        if (displayName.startsWith("animation.")) {
            displayName = displayName.substring("animation.".length());
        }
        toml.set("metadata.name", displayName);
        toml.set("metadata.duration", bbAnim.getOrElse("animation_length", 0.0));
        Object loopVal = bbAnim.get("loop");
        boolean looping = false;
        if (loopVal instanceof Boolean b) looping = b;
        else if (loopVal instanceof String s) looping = !s.equalsIgnoreCase("false");
        toml.set("metadata.looping", looping);

        // Layers
        toml.set("layers.default", 1.0);

        // Keyframes
        Config bones = bbAnim.get("bones");
        if (bones != null) {
            for (Config.Entry boneEntry : bones.entrySet()) {
                String boneName = boneEntry.getKey();
                if (!(boneEntry.getValue() instanceof Config bbBone)) continue;

                processTransformType(toml, "default", boneName, "position", bbBone.get("position"));
                processTransformType(toml, "default", boneName, "rotation", bbBone.get("rotation"));
                processTransformType(toml, "default", boneName, "offset", bbBone.get("offset"));
                processTransformType(toml, "default", boneName, "grow", bbBone.get("scale"));
            }
        }

        return toml;
    }

    private static void processTransformType(Config toml, String layer, String boneName, String type, Object transformData) {
        if (transformData == null) return;

        Config typeConfig = Config.inMemory();
        Config.setInsertionOrderPreserved(true);

        if (transformData instanceof List) {
            // Static transform
            addKeyframe(typeConfig, 0.0, (List<?>) transformData, "linear", type);
        } else if (transformData instanceof Config config) {
            // Animated transform
            TreeMap<Double, Object> sortedFrames = new TreeMap<>();
            for (Config.Entry entry : config.entrySet()) {
                try {
                    sortedFrames.put(Double.parseDouble(entry.getKey()), entry.getValue());
                } catch (NumberFormatException ignored) {}
            }

            for (Map.Entry<Double, Object> entry : sortedFrames.entrySet()) {
                Double time = entry.getKey();
                Object value = entry.getValue();
                String lerpMode = "linear";
                if (value instanceof Config valConfig) {
                    lerpMode = valConfig.getOrElse("lerp_mode", "linear");
                    if (valConfig.contains("pre")) {
                        // Handle pre/post
                        List<Number> pre = valConfig.get("pre");
                        List<Number> post = valConfig.get("post");
                        if (pre != null && time > EPSILON) {
                            addKeyframe(typeConfig, time - EPSILON, pre, lerpMode, type);
                        }
                        if (post != null) {
                            addKeyframe(typeConfig, time, post, lerpMode, type);
                        }
                    } else if (valConfig.contains("post")) {
                        List<Number> post = valConfig.get("post");
                        addKeyframe(typeConfig, time, post, lerpMode, type);
                    } else {
                        // Check for direct vector in config
                        List<Number> vector = valConfig.get("vector");
                        if (vector != null) {
                            addKeyframe(typeConfig, time, vector, lerpMode, type);
                        }
                    }
                } else if (value instanceof List list) {
                    addKeyframe(typeConfig, time, list, lerpMode, type);
                }
            }
        }

        if (!typeConfig.isEmpty()) {
            for (Config.Entry entry : typeConfig.entrySet()) {
                toml.set(List.of(layer, boneName, type, entry.getKey()), entry.getValue());
            }
        }
    }

    private static void addKeyframe(Config typeConfig, double time, List<?> values, String bbLerpMode, String type) {
        if (values.size() < 3) return;

        Config frame = Config.inMemory();
        List<Double> toValues = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Object val = values.get(i);
            if (val instanceof Number n) {
                double d = n.doubleValue();
                if (type.equals("rotation")) {
                    // Match BlockbenchModelConverter rotation flipping
                    if (i == 0 || i == 2) d = -d;
                } else if (type.equals("grow")) {
                    d -= 1.0;
                }
                toValues.add(d);
            } else {
                toValues.add(0.0);
            }
        }
        frame.set("to", toValues);

        List<String> interpolation = new ArrayList<>();
        interpolation.add("both");
        interpolation.add(mapLerpMode(bbLerpMode));
        frame.set("interpolation", interpolation);

        String timeStr = String.format(Locale.US, "%.4f", Math.max(0, time));
        typeConfig.set(List.of(timeStr), frame);
    }

    private static String mapLerpMode(String bbLerpMode) {
        if (bbLerpMode == null) return "linear";
        return switch (bbLerpMode.toLowerCase()) {
            case "step" -> "step";
            case "catmullrom" -> "catmullrom";
            default -> "linear";
        };
    }

    private static void saveTveAnimation(Config tomlConfig, Path outputPath) {
        try {
            Files.createDirectories(outputPath.getParent());
            TomlWriter writer = TomlFormat.instance().createWriter();
            writer.setIndent("\t");
            writer.setOmitIntermediateLevels(true);
            writer.setWriteTableInlinePredicate(c -> {
                // Inline only the leaf keyframe data (which contains "to")
                // and ensure it's actually a leaf (no nested Configs).
                if (c.contains("to")) {
                    return c.entrySet().stream().noneMatch(e -> e.getValue() instanceof Config);
                }
                return false;
            });
            String tomlString = writer.writeToString(tomlConfig);
            String processed = postProcessToml(tomlString);
            Files.writeString(outputPath, processed);
            System.out.println("Saved animation to: " + outputPath);
        } catch (IOException e) {
            System.err.println("Failed to save animation: " + e.getMessage());
        }
    }

    private static String postProcessToml(String toml) {
        String[] lines = toml.split("\\r?\\n");
        StringBuilder sb = new StringBuilder();
        String lastBonePath = null;

        for (int i = 0; i < lines.length; i++) {
            String line = lines[i];
            String trimmed = line.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            if (trimmed.startsWith("[")) {
                String header = trimmed;
                String currentBonePath = null;
                if (header.startsWith("[") && header.endsWith("]")) {
                    String path = header.substring(1, header.length() - 1);
                    String[] parts = path.split("\\.");
                    // Check for layer.bone.type (3 parts)
                    if (parts.length >= 3) {
                        currentBonePath = parts[0] + "." + parts[1];
                    }
                }

                if (i > 0) {
                    // Add newline if it's NOT the same bone as before
                    if (currentBonePath == null || !currentBonePath.equals(lastBonePath)) {
                        sb.append(System.lineSeparator());
                    }
                }

                sb.append(header).append(System.lineSeparator());
                lastBonePath = currentBonePath;
            } else {
                // It's a value, ensure exactly one tab of indentation
                sb.append("\t").append(trimmed).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }
}
