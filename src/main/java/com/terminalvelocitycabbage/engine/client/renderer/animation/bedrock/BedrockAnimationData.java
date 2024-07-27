package com.terminalvelocitycabbage.engine.client.renderer.animation.bedrock;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.json.JsonFormat;
import com.github.zafarkhaja.semver.Version;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.util.ConfigUtils;
import com.terminalvelocitycabbage.engine.util.Easing;
import com.terminalvelocitycabbage.engine.util.tuples.Triplet;
import org.joml.Vector3f;

import java.util.*;

public class BedrockAnimationData {

    Version formatVersion;
    Map<String, AnimationData> animations;

    public BedrockAnimationData(Version formatVersion, Map<String, AnimationData> animations) {
        this.formatVersion = formatVersion;
        this.animations = animations;
    }

    public void print() {
        Log.info(formatVersion);
        for (Map.Entry<String, AnimationData> entry : animations.entrySet()) {
            Log.info(entry.getKey() + " " + entry.getValue());
        }
    }

    public record BedrockBoneTransformationData(
            float percentStage,
            Vector3f simpleTransform,
            Easing.Function easing
    ) {

        @Override
        public String toString() {
            return "BedrockBoneTransformationData{" +
                    "percentStage=" + percentStage +
                    ", simpleTransform=" + simpleTransform +
                    ", easing=" + easing +
                    '}';
        }
    }

    public record AnimationData (
        boolean loop,
        float animationLength,
        float startDelay,
        float loopDelay,
        //Bone name, positions, rotations, scale
        Map<String, Triplet<
                List<BedrockBoneTransformationData>,
                List<BedrockBoneTransformationData>,
                List<BedrockBoneTransformationData>>> boneTransformationData
    ) {

        @Override
        public String toString() {
            return "AnimationData{" +
                    "loop=" + loop +
                    ", animationLength=" + animationLength +
                    ", startDelay=" + startDelay +
                    ", loopDelay=" + loopDelay +
                    ", boneTransformationData=" + boneTransformationData +
                    '}';
        }
    }

    public static class Loader {

        static Version formatVersion;
        static Map<String, AnimationData> animations = new HashMap<>();

        public static BedrockAnimationData loadAnimations(Resource animationsResource) {

            String resourceString = animationsResource.asString();

            ConfigFormat<?> jsonFormat = JsonFormat.fancyInstance();
            ConfigParser<?> parser = jsonFormat.createParser();
            Config config = parser.parse(resourceString);

            formatVersion = Version.parse(config.get("format_version"));

            Config animationsConfig = config.get("animations");
            animationsConfig.valueMap().forEach((key, value) -> parseAnimationConfig(key, (Config) value, animations));

            return new BedrockAnimationData(formatVersion, animations);
        }

        private static void parseAnimationConfig(String animationName, Config animation, Map<String, AnimationData> animationData) {

            AnimationData data = new AnimationData(
                    animation.get("loop"),
                    Float.parseFloat(animation.get("animation_length").toString()),
                    Float.parseFloat(animation.get("start_delay").toString()),
                    Float.parseFloat(animation.get("loop_delay").toString()),
                    parseBedrockBoneData(animation.get("bones"))
            );

            animationData.put(animationName, data);
        }

        //bone name, Position, Rotation, Scale
        private static Map<String, Triplet<
                List<BedrockBoneTransformationData>,
                List<BedrockBoneTransformationData>,
                List<BedrockBoneTransformationData>>> parseBedrockBoneData(Config bones) {

            Map<String, Triplet<
                    List<BedrockBoneTransformationData>,
                    List<BedrockBoneTransformationData>,
                    List<BedrockBoneTransformationData>>> boneTransformationData = new HashMap<>();

            bones.valueMap().forEach((key, value) -> {

                var bonesConfig = (Config) value;
                boneTransformationData.put(
                        key,
                        new Triplet<>(
                                parseTransformationData(bonesConfig.get("position")),
                                parseTransformationData(bonesConfig.get("rotation")),
                                parseTransformationData(bonesConfig.get("scale"))
                        )
                );
            });

            return boneTransformationData;
        }

        //TODO parse molang
        private static List<BedrockBoneTransformationData> parseTransformationData(Config transformationConfig) {

            if (transformationConfig == null) return null;

            List<BedrockBoneTransformationData> transformationData = new ArrayList<>();

            transformationConfig.valueMap().forEach((key, value) -> {

                //The default when it's not interpolated
                if (value.toString().startsWith("[")) {
                    transformationData.add(new BedrockBoneTransformationData(
                            Float.parseFloat(key),
                            ConfigUtils.numberListToVector3f((List<Number>) value),
                            Easing.Function.LINEAR
                    ));
                } else {
                    transformationData.add(new BedrockBoneTransformationData(
                            Float.parseFloat(key),
                            ConfigUtils.numberListToVector3f(((Config) value).get("post")), //TODO pre
                            switch (((Config) value).get("lerp_mode").toString()) {
                                case "linear" -> Easing.Function.LINEAR;
                                case "catmullrom" -> Easing.Function.CIRCULAR;
                                case "step" -> Easing.Function.STEP;
                                default -> Easing.Function.LINEAR;
                            }
                    ));
                }
            });

            return transformationData;
        }

    }

}
