package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.electronwill.nightconfig.core.Config;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.github.zafarkhaja.semver.Version;
import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.debug.Log;
import com.terminalvelocitycabbage.engine.filesystem.resources.Resource;
import com.terminalvelocitycabbage.engine.filesystem.resources.ResourceCategory;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.Easing;
import org.joml.Vector3f;

import java.util.*;

public record TVAnimation(
        TVAnimationMetadata metadata,
        Map<String, TVAnimationLayer> layers,
        Map<String, TVAnimationEvent> events
) {

    public static TVAnimation of(Identifier animationResource) {
        Resource resource = ClientBase.getInstance().getFileSystem().getResource(ResourceCategory.ANIMATION, animationResource);
        if (resource == null) {
            Log.crash("Could not find animation resource: " + animationResource);
            return null;
        }
        Config config = TomlFormat.instance().createParser().parse(resource.asString());
        return of(config);
    }

    public static TVAnimation of(Config config) {
        Config metadataConfig = config.get("metadata");
        TVAnimationMetadata metadata = new TVAnimationMetadata(
                Version.valueOf(metadataConfig.get("version")),
                metadataConfig.get("name"),
                ((Number) metadataConfig.get("duration")).floatValue(),
                metadataConfig.get("looping")
        );

        Map<String, TVAnimationLayer> layers = new HashMap<>();
        Config layersConfig = config.get("layers");
        if (layersConfig != null) {
            for (Config.Entry entry : layersConfig.entrySet()) {
                layers.put(entry.getKey(), new TVAnimationLayer(entry.getKey(), ((Number) entry.getValue()).floatValue(), new HashMap<>()));
            }
        }

        for (Config.Entry layerEntry : config.entrySet()) {
            String layerName = layerEntry.getKey();
            if (layerName.equals("metadata") || layerName.equals("layers") || layerName.equals("events") || layerName.equals("event")) continue;

            TVAnimationLayer layer = layers.get(layerName);
            if (layer == null) continue;

            Config layerConfig = layerEntry.getValue();
            for (Config.Entry boneEntry : layerConfig.entrySet()) {
                String boneName = boneEntry.getKey();
                Config boneConfig = boneEntry.getValue();

                Map<Float, TVAnimationBoneTransformation> positions = new TreeMap<>();
                Map<Float, TVAnimationBoneTransformation> offsets = new TreeMap<>();
                Map<Float, TVAnimationBoneTransformation> rotations = new TreeMap<>();
                Map<Float, TVAnimationBoneTransformation> grows = new TreeMap<>();

                parseTransformType(boneConfig.get("position"), positions);
                parseTransformType(boneConfig.get("offset"), offsets);
                parseTransformType(boneConfig.get("rotation"), rotations);
                parseTransformType(boneConfig.get("grow"), grows);

                layer.keyframes().put(boneName, new TVAnimationKeyframe(positions, offsets, rotations, grows));
            }
        }

        // Parse events
        Map<String, TVAnimationEvent> events = new HashMap<>();
        List<Config> eventsList = config.get("event");
        if (eventsList != null) {
            for (Config eventConfig : eventsList) {
                TVAnimationEvent event = new TVAnimationEvent(
                        eventConfig.get("name"),
                        Optional.ofNullable(eventConfig.get("layer")),
                        TVAnimationEventType.fromString(eventConfig.get("type")),
                        Optional.ofNullable(eventConfig.get("at")),
                        Optional.ofNullable(eventConfig.get("from")),
                        Optional.ofNullable(eventConfig.get("anchor"))
                );
                events.put(event.name(), event);
            }
        }

        return new TVAnimation(metadata, layers, events);
    }

    private static void parseTransformType(Config config, Map<Float, TVAnimationBoneTransformation> target) {
        if (config == null) return;
        for (Config.Entry entry : config.entrySet()) {
            float time = Float.parseFloat(entry.getKey());
            Config frameConfig = entry.getValue();
            List<Number> to = frameConfig.get("to");
            List<String> interpolation = frameConfig.get("interpolation");

            Easing.Direction direction = Easing.Direction.fromString(interpolation.get(0));
            Easing.Function function = Easing.Function.fromString(interpolation.get(1));

            Vector3f value = new Vector3f(to.get(0).floatValue(), to.get(1).floatValue(), to.get(2).floatValue());
            target.put(time, new TVAnimationBoneTransformation(direction, function, value));
        }
    }

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
            Easing.Direction direction,
            Easing.Function function,
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
