package com.terminalvelocitycabbage.engine.client.renderer.animation;

import org.joml.Matrix4f;

import java.util.HashMap;
import java.util.Map;

public class AnimationController {

    //TODO replace string with identifier once animation registry exists
    Map<String, Animation> animations;
    private final Map<String, Integer> boneIndexMap;
    Map<Integer, Matrix4f> boneTransformations;

    public AnimationController(Map<String, Animation> animations, Map<String, Integer> boneIndexMap) {
        this.animations = animations;
        this.boneIndexMap = boneIndexMap;
        boneTransformations = new HashMap<>();
        boneIndexMap.values().forEach(boneIndex -> boneTransformations.put(boneIndex, new Matrix4f()));
    }

    public void update(long deltaTime) {
        animations.values().forEach(animation -> animation.update(deltaTime));
    }

    public Animation getAnimation(String animationName) {
        return animations.get(animationName);
    }

    public void stopAll() {
        animations.values().forEach(Animation::stop);
    }

    public Map<Integer, Matrix4f> getBoneTransformations() {
        boneTransformations.values().forEach(Matrix4f::identity);
        //Loop through all animations to get their contribution to the bone transformations
        for (Animation animation : animations.values()) {
            //Get this animation's transformation matrix and add it to the resultant transformation matrix to effectively combine these transformations
            //TODO verify, likely a wrong assumption
            animation.getCurrentTransformations().forEach(
                    (boneName, boneTransformation) -> boneTransformations.get(boneIndexMap.get(boneName)).add(boneTransformation)
            );
        }
        return boneTransformations;
    }
}
