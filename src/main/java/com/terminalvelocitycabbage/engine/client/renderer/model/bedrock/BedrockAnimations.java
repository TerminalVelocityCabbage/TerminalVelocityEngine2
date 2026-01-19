package com.terminalvelocitycabbage.engine.client.renderer.model.bedrock;

import java.util.Map;

public class BedrockAnimations {
    public String format_version;
    public Map<String, Animation> animations;

    public static class Animation {
        public float animation_length;
        public boolean loop;
        public Map<String, BoneAnimation> bones;
    }

    public static class BoneAnimation {
        public Object position;
        public Object rotation;
        public Object scale;
    }

    public static class Keyframe {
        public Object post;
        public String lerp_mode;
    }
}
