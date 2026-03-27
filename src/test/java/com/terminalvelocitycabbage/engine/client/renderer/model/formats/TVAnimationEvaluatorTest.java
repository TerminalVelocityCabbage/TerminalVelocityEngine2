package com.terminalvelocitycabbage.engine.client.renderer.model.formats;

import com.terminalvelocitycabbage.engine.client.renderer.model.Skeleton;
import com.terminalvelocitycabbage.engine.util.Easing;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TVAnimationEvaluatorTest {

    @Test
    public void testOffsetInSkeleton() {
        Map<String, Skeleton.SkeletonBone> bones = new HashMap<>();
        // Bone with offset [0, 1, 0]
        bones.put("root", new Skeleton.SkeletonBone("root", Optional.empty(), new Vector3f(0, 0, 0), new Vector3f(0, 1, 0), new Vector3f(0, 0, 0)));
        Map<String, Integer> boneIndices = new HashMap<>();
        boneIndices.put("root", 0);
        Skeleton skeleton = new Skeleton(bones, boneIndices, null);

        Matrix4f[] matrices = TVAnimationEvaluator.evaluate((TVAnimation) null, 0, skeleton);
        Matrix4f rootMatrix = matrices[0];

        Vector3f translation = new Vector3f();
        rootMatrix.getTranslation(translation);
        
        assertEquals(new Vector3f(0, 1, 0), translation);
    }

    @Test
    public void testOffsetInAnimation() {
        Map<String, Skeleton.SkeletonBone> bones = new HashMap<>();
        bones.put("root", new Skeleton.SkeletonBone("root", Optional.empty(), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(0, 0, 0)));
        Map<String, Integer> boneIndices = new HashMap<>();
        boneIndices.put("root", 0);
        Skeleton skeleton = new Skeleton(bones, boneIndices, null);

        // Create an animation with offset [0, 2, 0]
        Map<Float, TVAnimation.TVAnimationBoneTransformation> offsets = new TreeMap<>();
        offsets.put(0.0f, new TVAnimation.TVAnimationBoneTransformation(Easing.Direction.IN_OUT, Easing.Function.LINEAR, new Vector3f(0, 2, 0)));
        
        TVAnimation.TVAnimationKeyframe keyframe = new TVAnimation.TVAnimationKeyframe(new TreeMap<>(), offsets, new TreeMap<>(), new TreeMap<>());
        
        Map<String, TVAnimation.TVAnimationKeyframe> boneKeyframes = new HashMap<>();
        boneKeyframes.put("root", keyframe);
        
        TVAnimation.TVAnimationLayer layer = new TVAnimation.TVAnimationLayer("default", 1.0f, boneKeyframes);
        
        Map<String, TVAnimation.TVAnimationLayer> layers = new HashMap<>();
        layers.put("default", layer);
        
        TVAnimation animation = new TVAnimation(new TVAnimation.TVAnimationMetadata(null, "test", 1.0f, false), layers, new HashMap<>());

        Matrix4f[] matrices = TVAnimationEvaluator.evaluate(animation, 0, skeleton);
        Matrix4f rootMatrix = matrices[0];

        Vector3f translation = new Vector3f();
        rootMatrix.getTranslation(translation);
        
        assertEquals(new Vector3f(0, 2, 0), translation);
    }

    @Test
    public void testTransformationOrder() {
        Map<String, Skeleton.SkeletonBone> bones = new HashMap<>();
        // Bone with rotation 90 deg around Z and offset [1, 0, 0]
        bones.put("root", new Skeleton.SkeletonBone("root", Optional.empty(), new Vector3f(0, 0, 0), new Vector3f(1, 0, 0), new Vector3f(0, 0, 90)));
        Map<String, Integer> boneIndices = new HashMap<>();
        boneIndices.put("root", 0);
        Skeleton skeleton = new Skeleton(bones, boneIndices, null);

        Matrix4f[] matrices = TVAnimationEvaluator.evaluate((TVAnimation) null, 0, skeleton);
        Matrix4f rootMatrix = matrices[0];

        Vector4f point = new Vector4f(0, 0, 0, 1).mul(rootMatrix);

        // Expected: T(0,0,0) * Rz(90) * T(1,0,0) * [0,0,0] = Rz(90) * [1,0,0] = [0, 1, 0]
        assertEquals(0.0f, point.x, 0.001f);
        assertEquals(1.0f, point.y, 0.001f);
        assertEquals(0.0f, point.z, 0.001f);
    }
}
