package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.terminalvelocitycabbage.engine.client.ClientBase;
import com.terminalvelocitycabbage.engine.client.renderer.model.Skeleton;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.Easing;
import com.terminalvelocitycabbage.templates.ecs.components.AnimationControllerComponent;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Map;

//TODO refactor this to reduce allocations - this may get expensive fast
public class TVAnimationEvaluator {

    public static Matrix4f[] evaluate(TVAnimation animation, float time, Skeleton skeleton) {
        Matrix4f[] boneMatrices = new Matrix4f[skeleton.bones().size()];

        for (String boneName : skeleton.bones().keySet()) {
            evaluateBone(boneName, animation, time, skeleton, boneMatrices);
        }

        return boneMatrices;
    }

    private static Matrix4f evaluateBone(String boneName, TVAnimation animation, float time, Skeleton skeleton, Matrix4f[] boneMatrices) {
        int boneIndex = skeleton.boneIndices().get(boneName);
        if (boneMatrices[boneIndex] != null) return boneMatrices[boneIndex];

        Skeleton.SkeletonBone bone = skeleton.bones().get(boneName);
        Matrix4f localTransform = calculateLocalTransform(bone);

        if (animation != null) {
            float duration = animation.metadata().duration();
            float t = time % duration;
            TVAnimation.TVAnimationLayer defaultLayer = animation.layers().get("default");
            if (defaultLayer != null) {
                TVAnimation.TVAnimationKeyframe keyframes = defaultLayer.keyframes().get(boneName);
                if (keyframes != null) {
                    Vector3f animPos = evaluateTransform(keyframes.positions(), t);
                    Vector3f animRot = evaluateTransform(keyframes.rotations(), t);
                    Vector3f animScale = evaluateTransform(keyframes.grows(), t);

                    applyAnimationToTransform(localTransform, animPos, animRot, animScale);
                }
            }
        }

        Matrix4f parentTransform = new Matrix4f();
        if (bone.parent().isPresent() && skeleton.bones().containsKey(bone.parent().get())) {
            parentTransform = evaluateBone(bone.parent().get(), animation, time, skeleton, boneMatrices);
        }

        Matrix4f globalTransform = new Matrix4f(parentTransform).mul(localTransform);
        boneMatrices[boneIndex] = globalTransform;
        return globalTransform;
    }

    public static Matrix4f[] evaluate(AnimationControllerComponent component, Skeleton skeleton) {
        Matrix4f[] boneMatrices = new Matrix4f[skeleton.bones().size()];
        var animationRegistry = ClientBase.getInstance().getTvAnimationRegistry();

        for (String boneName : skeleton.bones().keySet()) {
            evaluateBone(boneName, component, skeleton, boneMatrices, animationRegistry);
        }

        return boneMatrices;
    }

    private static Matrix4f evaluateBone(String boneName, AnimationControllerComponent component, Skeleton skeleton, Matrix4f[] boneMatrices, com.terminalvelocitycabbage.engine.registry.Registry<TVAnimation> animationRegistry) {
        int boneIndex = skeleton.boneIndices().get(boneName);
        if (boneMatrices[boneIndex] != null) return boneMatrices[boneIndex];

        Skeleton.SkeletonBone bone = skeleton.bones().get(boneName);
        Matrix4f localTransform = calculateLocalTransform(bone);

        Vector3f totalAnimPos = new Vector3f(0, 0, 0);
        Vector3f totalAnimRot = new Vector3f(0, 0, 0);
        Vector3f totalAnimScale = new Vector3f(0, 0, 0);

        for (var stateEntry : component.getAnimationStates().entrySet()) {
            String animName = stateEntry.getKey();
            var state = stateEntry.getValue();
            if (state.getInfluence() <= 0) continue;

            TVAnimation animation = animationRegistry.get(Identifier.fromString(animName, "animation"));
            if (animation == null) {
                continue;
            }

            float duration = animation.metadata().duration();
            float t = duration == 0 ? 0 : animation.metadata().looping() ? state.getCurrentTime() % duration : Math.min(state.getCurrentTime(), duration);
            TVAnimation.TVAnimationLayer defaultLayer = animation.layers().get("default");
            if (defaultLayer != null) {
                TVAnimation.TVAnimationKeyframe keyframes = defaultLayer.keyframes().get(boneName);
                if (keyframes != null) {
                    Vector3f animPos = evaluateTransform(keyframes.positions(), t);
                    Vector3f animRot = evaluateTransform(keyframes.rotations(), t);
                    Vector3f animScale = evaluateTransform(keyframes.grows(), t);

                    totalAnimPos.add(new Vector3f(animPos).mul(state.getInfluence()));
                    totalAnimRot.add(new Vector3f(animRot).mul(state.getInfluence()));
                    totalAnimScale.add(new Vector3f(animScale).mul(state.getInfluence()));
                }
            }
        }

        applyAnimationToTransform(localTransform, totalAnimPos, totalAnimRot, totalAnimScale);

        Matrix4f parentTransform = new Matrix4f();
        if (bone.parent().isPresent() && skeleton.bones().containsKey(bone.parent().get())) {
            parentTransform = evaluateBone(bone.parent().get(), component, skeleton, boneMatrices, animationRegistry);
        }

        Matrix4f globalTransform = new Matrix4f(parentTransform).mul(localTransform);
        boneMatrices[boneIndex] = globalTransform;
        return globalTransform;
    }

    private static Matrix4f calculateLocalTransform(Skeleton.SkeletonBone bone) {
        Matrix4f localTransform = new Matrix4f();
        localTransform.translate(bone.position());
        localTransform.rotateZYX((float) Math.toRadians(bone.rotation().z()), (float) Math.toRadians(bone.rotation().y()), (float) Math.toRadians(bone.rotation().x()));
        return localTransform;
    }

    private static void applyAnimationToTransform(Matrix4f localTransform, Vector3f animPos, Vector3f animRot, Vector3f animScale) {
        localTransform.translate(animPos);
        localTransform.rotateZYX((float) Math.toRadians(animRot.z()), (float) Math.toRadians(animRot.y()), (float) Math.toRadians(animRot.x()));
        localTransform.scale(new Vector3f(animScale).add(1.0f, 1.0f, 1.0f));
    }

    private static Vector3f evaluateTransform(Map<Float, TVAnimation.TVAnimationBoneTransformation> keyframes, float t) {
        if (keyframes.isEmpty()) return new Vector3f(0, 0, 0);

        Map.Entry<Float, TVAnimation.TVAnimationBoneTransformation> prev = null;
        Map.Entry<Float, TVAnimation.TVAnimationBoneTransformation> next = null;

        for (var entry : keyframes.entrySet()) {
            if (entry.getKey() <= t) {
                prev = entry;
            } else {
                next = entry;
                break;
            }
        }

        // Handle looping for catmullrom or just smooth wrap
        if (prev == null) {
            // If we are before the first keyframe, we might want to wrap to the last one if looping
            // But for now let's just use the first keyframe's value
            return keyframes.values().iterator().next().value();
        }
        if (next == null) {
            // If we are after the last keyframe, we use the last keyframe's value
            return prev.getValue().value();
        }

        float progress = (t - prev.getKey()) / (next.getKey() - prev.getKey());

        Vector3f start = prev.getValue().value();
        Vector3f end = next.getValue().value();

        // Use the easing function defined at the TARGET keyframe (next)
        Easing.Direction direction = next.getValue().direction();
        Easing.Function function = next.getValue().function();

        float e = Easing.ease(direction, function, progress);
        return new Vector3f(start).lerp(end, e);
    }
}
