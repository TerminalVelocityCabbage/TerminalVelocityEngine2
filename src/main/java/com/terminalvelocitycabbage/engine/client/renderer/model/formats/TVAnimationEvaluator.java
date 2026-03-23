package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.terminalvelocitycabbage.engine.util.Easing;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Map;
import java.util.TreeMap;

public class TVAnimationEvaluator {

    public static Matrix4f[] evaluate(TVAnimation animation, float time, TVModel model) {
        Matrix4f[] boneMatrices = new Matrix4f[model.bones().size()];

        float duration = animation == null ? 1.0f : animation.metadata().duration();
        float t = animation == null ? 0.0f : time % duration;

        // For each bone in the model, calculate its global transformation at time T
        // We'll iterate through bones in their order in the model.
        // Since parents always come before children in the TVModel bone map (LinkedHashMap),
        // we can calculate parent transforms before children.

        int i = 0;
        for (var entry : model.bones().entrySet()) {
            String boneName = entry.getKey();
            TVModel.TVModelBone bone = entry.getValue();

            // Calculate local transformation at time T
            Matrix4f localTransform = new Matrix4f();

            // Base bone transformation
            localTransform.translate(bone.position());
            localTransform.rotateZYX((float) Math.toRadians(bone.rotation().z()), (float) Math.toRadians(bone.rotation().y()), (float) Math.toRadians(bone.rotation().x()));

            // Apply animation keyframes (only support "default" layer for now)
            if (animation != null) {
                TVAnimation.TVAnimationLayer defaultLayer = animation.layers().get("default");
                if (defaultLayer != null) {
                    TVAnimation.TVAnimationKeyframe keyframes = defaultLayer.keyframes().get(boneName);
                    if (keyframes != null) {
                        Vector3f animPos = evaluateTransform(keyframes.positions(), t);
                        Vector3f animRot = evaluateTransform(keyframes.rotations(), t);
                        Vector3f animScale = evaluateTransform(keyframes.grows(), t);

                        localTransform.translate(animPos);
                        localTransform.rotateZYX((float) Math.toRadians(animRot.z()), (float) Math.toRadians(animRot.y()), (float) Math.toRadians(animRot.x()));
                        localTransform.scale(new Vector3f(animScale).add(1.0f, 1.0f, 1.0f));
                    }
                }
            }

            // Global transform = ParentGlobalTransform * LocalTransform
            Matrix4f globalTransform;
            if (bone.parent().isPresent() && model.bones().containsKey(bone.parent().get())) {
                Integer parentIndex = model.boneIndices().get(bone.parent().get());
                if (parentIndex != null && boneMatrices[parentIndex] != null) {
                    globalTransform = new Matrix4f(boneMatrices[parentIndex]).mul(localTransform);
                } else {
                    // Log an error if the parent hasn't been processed yet, but don't crash.
                    // Instead just use the local transform.
                    globalTransform = localTransform;
                }
            } else {
                globalTransform = localTransform;
            }

            boneMatrices[i] = globalTransform;
            i++;
        }

        return boneMatrices;
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
