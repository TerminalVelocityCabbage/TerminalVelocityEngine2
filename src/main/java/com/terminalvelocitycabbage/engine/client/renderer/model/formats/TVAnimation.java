package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.github.zafarkhaja.semver.Version;
import com.terminalvelocitycabbage.engine.util.Easing;
import org.joml.Vector3f;

import java.util.Map;
import java.util.Optional;

public record TVAnimation(
        TVAnimationMetadata metadata,
        Map<String, TVAnimationLayer> layers,
        Map<String, TVAnimationKeyframe> keyframes,
        Map<String, TVAnimationEvent> events
) {

    public record TVAnimationMetadata(
            Version version,
            String name,
            float duration, //The length of this animation in seconds
            boolean looping
    ) { }

    public record TVAnimationLayer(
            String name,
            float influence,
            Map<String, TVAnimationKeyframe> keyframes //Bone name -> keyframes
    ) { }

    public record TVAnimationKeyframe(
            //A map of end time -> transformation for each transformation attribute
            Map<Float, TVAnimationBoneTransformation> positions,
            Map<Float, TVAnimationBoneTransformation> offsets,
            Map<Float, TVAnimationBoneTransformation> rotations,
            Map<Float, TVAnimationBoneTransformation> grows
    ) { }

    public record TVAnimationBoneTransformation(
            Optional<Easing> easeIn,
            Optional<Easing> easeOut,
            Vector3f value
    ) { }

    public enum TVAnimationEventType {

        SOUND,
        GENERIC;

        public static TVAnimationEventType fromString(String type) {
            return switch(type) {
                case "sound" -> SOUND;
                case "generic" -> GENERIC;
                default -> throw new IllegalArgumentException("Invalid animation event type: " + type);
            };
        }
    }

    public record TVAnimationEvent(
            String name,
            Optional<String> layer,
            TVAnimationEventType type,
            Optional<Float> at, //The time at which this event should be published
            Optional<Float> from, //The timeframe for which this event will evaluate as "active"
            Optional<String> anchor
    ) { }
}
