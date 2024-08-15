package com.terminalvelocitycabbage.engine.client.renderer.animation;

import com.terminalvelocitycabbage.engine.util.tuples.Triplet;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class AnimationController {

    //TODO replace string with identifier once animation registry exists
    Map<String, Animation> animations;
    private final Map<String, Integer> boneIndexMap;
    Map<Integer, Triplet<Vector3f, Vector3f, Vector3f>> boneTransformations; //Pos, Rot, Scale
    Map<Integer, Matrix4f> boneTransformationMatrices;

    public AnimationController(Map<String, Animation> animations, Map<String, Integer> boneIndexMap) {
        this.animations = animations;
        this.boneIndexMap = boneIndexMap;
        boneTransformations = new HashMap<>();
        boneTransformationMatrices = new HashMap<>();
        boneIndexMap.values().forEach(boneIndex -> boneTransformations.put(boneIndex, new Triplet<>(new Vector3f(), new Vector3f(), new Vector3f(1))));
        boneIndexMap.values().forEach(boneIndex -> boneTransformationMatrices.put(boneIndex, new Matrix4f()));
    }

    public void update(long deltaTime) {
        //Reset all transformations from last frame
        boneTransformations.values().forEach(vector3fVector3fVector3fTriplet -> {
            vector3fVector3fVector3fTriplet.getValue0().zero();
            vector3fVector3fVector3fTriplet.getValue1().zero();
            vector3fVector3fVector3fTriplet.getValue2().set(1);
        });
        boneTransformationMatrices.values().forEach(Matrix4f::identity);

        //Loop through all animations update them and get their contribution to the bone transformations
        for (Animation animation : animations.values()) {
            animation.update(deltaTime);
            //Get this animation's transformations add them together
            animation.getCurrentTransformations().forEach(
                    (boneName, boneTransformation) -> {
                        boneTransformations.get(boneIndexMap.get(boneName)).getValue0().add(boneTransformation.getValue0());
                        boneTransformations.get(boneIndexMap.get(boneName)).getValue1().add(boneTransformation.getValue1());
                        boneTransformations.get(boneIndexMap.get(boneName)).getValue2().mul(boneTransformation.getValue2());
                    }
            );
        }
        //Convert all of these updated and combined transformations into a single transformation matrix for each bone
        for (int i = 0; i < boneTransformationMatrices.size(); i++) {
            var boneTransformation = boneTransformations.get(i);
            var eulerRotation = boneTransformation.getValue1();
            var rotation = new Quaternionf().rotateXYZ((float) Math.toRadians(eulerRotation.x), (float) Math.toRadians(eulerRotation.y), (float) Math.toRadians(eulerRotation.z));
            boneTransformationMatrices.get(i)
                    .identity()
                    .translationRotateScale(boneTransformation.getValue0(), rotation, boneTransformation.getValue2());
        }
    }

    public Animation getAnimation(String animationName) {
        return animations.get(animationName);
    }

    public void stopAll() {
        animations.values().forEach(Animation::stop);
    }

    public Map<Integer, Matrix4f> getBoneTransformations() {
        return boneTransformationMatrices;
    }
}
