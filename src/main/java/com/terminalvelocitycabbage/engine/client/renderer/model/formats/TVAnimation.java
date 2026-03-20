package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.github.zafarkhaja.semver.Version;
import com.terminalvelocitycabbage.engine.util.Easing;
import com.terminalvelocitycabbage.engine.util.touples.Pair;
import org.joml.Vector2i;
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
            int duration,
            int tickrate,
            boolean looping
    ) { }

    public record TVAnimationLayer(
            String name,
            float influence
    ) { }

    public record TVAnimationKeyframe(
            Optional<String> layer,
            Vector2i timeframe, //Start time and end time in ticks
            Map<String, TVAnimationBoneTransformation> transformations //Bone name -> transformations
    ) { }

    public record TVAnimationBoneTransformation(
            Optional<Easing> easeIn,
            Optional<Easing> easeOut,
            Optional<Vector3f> translation, //Modifies position attribute
            Optional<Vector3f> offset, //Modifies offset attribute
            Optional<Vector3f> rotation, //Modifies rotation attribute
            Optional<Vector3f> grow //Modifies grow attribute
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
            Pair<Integer, Optional<Integer>> duration,
            Optional<String> anchor
    ) { }
}
