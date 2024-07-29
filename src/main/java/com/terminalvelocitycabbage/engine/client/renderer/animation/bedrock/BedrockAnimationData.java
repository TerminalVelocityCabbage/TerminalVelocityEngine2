package com.terminalvelocitycabbage.engine.client.renderer.animation.bedrock;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.io.ConfigParser;
import com.electronwill.nightconfig.json.JsonFormat;
import com.github.zafarkhaja.semver.Version;
import com.terminalvelocitycabbage.engine.client.renderer.animation.Animation;
import com.terminalvelocitycabbage.engine.client.renderer.animation.Keyframe;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.util.ConfigUtils;
import com.terminalvelocitycabbage.engine.util.Easing;
import org.joml.Vector3f;

import java.util.*;

public class BedrockAnimationData {

    Version formatVersion;
    //Animation name, Animation data
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

    public Map<String, Animation> toAnimations() {

        Map<String, Animation> convertedAnimations = new HashMap<>();
        Map<String, List<Keyframe>> keyframes;
        for (Map.Entry<String, AnimationData> entry : animations.entrySet()) {
            var animationName = entry.getKey();
            var data = entry.getValue();

            //Convert bedrock transformation data into keyframes
            keyframes = new HashMap<>();

            for (Map.Entry<String, List<Keyframe>> boneData : data.boneKeyframes.entrySet()) {

                var boneName = boneData.getKey();
                var boneKeyframes = boneData.getValue();

                keyframes.put(boneName, boneKeyframes);
            }

            Animation animation = new Animation(
                    Math.round(data.startDelay * 1000f),
                    Math.round(data.animationLength * 1000f),
                    Math.round(data.loopDelay * 1000f),
                    keyframes
            );

            convertedAnimations.put(animationName, animation);
        }

        return convertedAnimations;
    }

    public record AnimationData (
        boolean loop,
        float animationLength,
        float startDelay,
        float loopDelay,
        //Bone name, keyframes
        Map<String, List<Keyframe>> boneKeyframes
    ) {

        @Override
        public String toString() {
            return "AnimationData{" +
                    "loop=" + loop +
                    ", animationLength=" + animationLength +
                    ", startDelay=" + startDelay +
                    ", loopDelay=" + loopDelay +
                    ", boneKeyframes=" + boneKeyframes +
                    '}';
        }
    }

    public static class Loader {

        static Version formatVersion;
        static Map<String, AnimationData> animations = new HashMap<>();

        public static BedrockAnimationData loadAnimations(Resource animationsResource) {

            String resourceString = animationsResource.asString();

            //Not sure where best to have this call, but it is needed to be called sometime before reading animations.
            Config.setInsertionOrderPreserved(true);
            ConfigFormat<?> jsonFormat = JsonFormat.fancyInstance();
            ConfigParser<?> parser = jsonFormat.createParser();
            Config config = parser.parse(resourceString);

            formatVersion = Version.parse(config.get("format_version"));

            Config animationsConfig = config.get("animations");
            animationsConfig.valueMap().forEach((key, value) -> parseAnimationConfig(key, (Config) value, animations));

            return new BedrockAnimationData(formatVersion, animations);
        }

        private static void parseAnimationConfig(String animationName, Config animation, Map<String, AnimationData> animationData) {

            var animationLength = Float.parseFloat(animation.get("animation_length").toString());
            AnimationData data = new AnimationData(
                    animation.get("loop"),
                    animationLength,
                    Float.parseFloat(animation.get("start_delay").toString()),
                    Float.parseFloat(animation.get("loop_delay").toString()),
                    parseBedrockBoneData(animation.get("bones"), animationLength)
            );

            animationData.put(animationName, data);
        }

        //bone name, Position, Rotation, Scale
        private static Map<String, List<Keyframe>> parseBedrockBoneData(Config bones, float animationLength) {

            Map<String, List<Keyframe>> boneKeyframeMap = new HashMap<>();

            bones.valueMap().forEach((key, value) -> {

                var bonesConfig = (Config) value;
                List<Keyframe> keyframes = new ArrayList<>();

                keyframes.addAll(parseTransformationData(Keyframe.Component.POSITION, bonesConfig.get("position"), animationLength));
                keyframes.addAll(parseTransformationData(Keyframe.Component.ROTATION, bonesConfig.get("rotation"), animationLength));
                keyframes.addAll(parseTransformationData(Keyframe.Component.SCALE, bonesConfig.get("scale"), animationLength));

                boneKeyframeMap.put(key, keyframes);
            });

            return boneKeyframeMap;
        }

        //TODO parse molang
        private static List<Keyframe> parseTransformationData(Keyframe.Component component, Config transformationConfig, float endTime) {

            if (transformationConfig == null) return Collections.emptyList();

            List<Keyframe> keyframes = new ArrayList<>();
            int iteration = 0;
            for (Map.Entry<String, Object> entry : transformationConfig.valueMap().entrySet()) {
                //Easier entry names
                String endTimeSeconds = entry.getKey();
                Object keyframeConfigOrTransformation = entry.getValue();

                //The previous keyframes final transformation to use used as this keyframe's start transformation
                Vector3f previousKeyframeEndTransformation;
                //The previous keyframes end time to be used as this keyframes start time
                float previousKeyframeEndTime;
                //If this is the first iteration we assume this is the first keyframe
                if (iteration == 0) {
                    //Some default values for the first keyframe (this keyframe gets deleted at the end since it's just setup)
                    previousKeyframeEndTransformation = new Vector3f();
                    previousKeyframeEndTime = 0f;
                } else {
                    //Get the previous keyframe and some info about it to use for this keyframe
                    var previousKeyframe = keyframes.get(iteration - 1);
                    previousKeyframeEndTransformation = previousKeyframe.getEndTransformation();
                    previousKeyframeEndTime = previousKeyframe.getEndTime();
                }
                var endTimeMillis = Float.parseFloat(endTimeSeconds) * 1000;

                //Create the keyframes
                if (keyframeConfigOrTransformation.toString().startsWith("[")) { //linear is simplified out in bbmodel
                    keyframes.add(new Keyframe(
                            component,
                            previousKeyframeEndTransformation,
                            ConfigUtils.numberListToVector3f((List<Number>) keyframeConfigOrTransformation),
                            Easing.Function.LINEAR,
                            previousKeyframeEndTime,
                            endTimeMillis
                    ));
                } else { //Usually Non-linear interpolation
                    keyframes.add(new Keyframe(
                            component,
                            previousKeyframeEndTransformation,
                            ConfigUtils.numberListToVector3f(((Config) keyframeConfigOrTransformation).get("post")), //TODO pre
                            switch (((Config) keyframeConfigOrTransformation).get("lerp_mode").toString()) {
                                case "catmullrom" -> Easing.Function.CIRCULAR;
                                case "step" -> Easing.Function.STEP;
                                default -> Easing.Function.LINEAR;
                            },
                            previousKeyframeEndTime,
                            endTimeMillis
                    ));
                }
                iteration++;
            }
            //The first keyframe is really only to set the stage for the second keyframe to start in the right place
            keyframes.removeFirst();

            return keyframes;
        }

    }

}
