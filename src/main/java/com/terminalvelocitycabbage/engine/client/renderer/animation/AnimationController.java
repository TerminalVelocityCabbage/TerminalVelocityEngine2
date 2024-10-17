package com.terminalvelocitycabbage.engine.client.renderer.animation;

import com.terminalvelocitycabbage.engine.client.renderer.model.Model;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

public class AnimationController {

    //TODO replace string with identifier once animation registry exists
    private final Map<String, Animation> animations;
    private final Map<String, Model.Bone> bonesMap;
    private final Map<Integer, Model.Bone> boneIndexMap;
    private final Map<Integer, TransformationSnapshot> boneTransformations;
    private final Map<Integer, Matrix4f> boneTransformationMatrices;

    public AnimationController(Map<String, Animation> animations, Map<String, Model.Bone> bonesMap) {
        this.animations = animations;
        this.bonesMap = bonesMap;
        boneIndexMap = new HashMap<>();
        boneTransformations = new HashMap<>();
        boneTransformationMatrices = new HashMap<>();
        bonesMap.values().forEach(bone -> {
            boneIndexMap.put(bone.getBoneIndex(), bone);
            boneTransformations.put(bone.getBoneIndex(), new TransformationSnapshot(new Vector3f(), new Vector3f(), new Vector3f(1)));
            boneTransformationMatrices.put(bone.getBoneIndex(), new Matrix4f());
        });
    }

    public void update(long deltaTime, Model model) {
        //Reset all transformations from last frame
        boneTransformations.values().forEach(transformationSnapshot -> {
            transformationSnapshot.position().zero();
            transformationSnapshot.rotation().zero();
            transformationSnapshot.scale().set(1);
        });
        boneTransformationMatrices.values().forEach(Matrix4f::identity);

        //Loop through all animations update them and get their contribution to the bone transformations
        for (Animation animation : animations.values()) {
            animation.update(deltaTime);
            //Get this animation's transformations add them together
            animation.getCurrentTransformations().forEach(
                    (boneName, boneTransformation) -> {
                        var transformation = boneTransformations.get(bonesMap.get(boneName).getBoneIndex());
                        transformation.position().add(boneTransformation.position());
                        transformation.rotation().add(boneTransformation.rotation());
                        transformation.scale().mul(boneTransformation.scale());
                    }
            );
        }
        //Convert all of these updated and combined transformations into a single transformation matrix for each bone
        for (Map.Entry<Integer, Model.Bone> entry : boneIndexMap.entrySet()) {

            var index = entry.getKey();
            var bone = entry.getValue();

            var boneTransformation = boneTransformations.get(index);
            var eulerRotation = boneTransformation.rotation();
            var rotation = new Quaternionf().rotateXYZ(
                    (float) Math.toRadians(eulerRotation.x),
                    (float) Math.toRadians(eulerRotation.y),
                    (float) Math.toRadians(eulerRotation.z)
            );
            boneTransformationMatrices.get(index)
                    .identity()
                    .scale(boneTransformation.scale())
                    .rotateAroundLocal(rotation, bone.getPivotPoint().x, bone.getPivotPoint().y, bone.getPivotPoint().z)
                    .translate(boneTransformation.position());
                    //.translate(bone.getOffset());
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
