package com.terminalvelocitycabbage.engine.client.renderer.model.bedrock;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AnimationSystemTest {

    @Test
    public void testAnimationUpdate() {
        AnimationSystem system = new AnimationSystem();
        AnimationComponent animComp = new AnimationComponent();
        
        // Setup Geometry
        BedrockGeometry geometry = new BedrockGeometry();
        geometry.minecraft_geometry = new ArrayList<>();
        BedrockGeometry.GeometryData geoData = new BedrockGeometry.GeometryData();
        geoData.bones = new ArrayList<>();
        
        BedrockGeometry.Bone rootBone = new BedrockGeometry.Bone();
        rootBone.name = "root";
        rootBone.pivot = List.of(0f, 0f, 0f);
        geoData.bones.add(rootBone);
        
        BedrockGeometry.Bone childBone = new BedrockGeometry.Bone();
        childBone.name = "child";
        childBone.parent = "root";
        childBone.pivot = List.of(0f, 5f, 0f);
        geoData.bones.add(childBone);
        
        geometry.minecraft_geometry.add(geoData);
        animComp.geometry = geometry;
        
        animComp.boneIndexMap = new HashMap<>();
        animComp.boneIndexMap.put("root", 0);
        animComp.boneIndexMap.put("child", 1);
        
        // Setup Animation
        BedrockAnimations animations = new BedrockAnimations();
        animations.animations = new HashMap<>();
        BedrockAnimations.Animation animation = new BedrockAnimations.Animation();
        animation.animation_length = 2.0f;
        animation.loop = true;
        animation.bones = new HashMap<>();
        
        BedrockAnimations.BoneAnimation rootAnim = new BedrockAnimations.BoneAnimation();
        Map<String, Object> rootRotations = new HashMap<>();
        rootRotations.put("0.0", List.of(0, 0, 0));
        rootRotations.put("1.0", List.of(90, 0, 0));
        rootRotations.put("2.0", List.of(0, 0, 0));
        rootAnim.rotation = rootRotations;
        animation.bones.put("root", rootAnim);
        
        animations.animations.put("test_anim", animation);
        animComp.animations = animations;
        animComp.currentAnimation = "test_anim";
        animComp.playing = true;
        
        // Initial update (time 0)
        system.updateAnimation(animComp, 0);
        assertNotNull(animComp.boneTransforms);
        assertEquals(2, animComp.boneTransforms.length);
        
        Matrix4f rootId = new Matrix4f();
        // At time 0, rotation is 0, so it should be identity (since pivot is 0,0,0)
        assertTrue(animComp.boneTransforms[0].equals(rootId, 0.0001f), "Root should be identity at t=0");
        
        // Update to time 1.0
        system.updateAnimation(animComp, 1.0f);
        assertEquals(1.0f, animComp.currentTime);
        
        // At time 1.0, root has 90 deg rotation on X
        Matrix4f expectedRoot = new Matrix4f().rotateX((float) Math.toRadians(90));
        assertTrue(animComp.boneTransforms[0].equals(expectedRoot, 0.0001f), "Root transform incorrect at t=1.0. Expected: " + expectedRoot + " but got: " + animComp.boneTransforms[0]);
        
        // Child bone at t=1.0 has no animation, so it uses bind pose relative to root.
        // World Child = LocalRoot * LocalChild = R(90) * Identity = R(90)
        assertTrue(animComp.boneTransforms[1].equals(expectedRoot, 0.0001f), "Child transform incorrect at t=1.0. Expected: " + expectedRoot + " but got: " + animComp.boneTransforms[1]);

        // Update to time 0.5
        // Root rotation should be 45 deg on X (interpolated between 0 and 90)
        animComp.currentTime = 0; // reset for easier calculation
        system.updateAnimation(animComp, 0.5f);
        Matrix4f expectedRoot45 = new Matrix4f().rotateX((float) Math.toRadians(45));
        assertTrue(animComp.boneTransforms[0].equals(expectedRoot45, 0.0001f), "Root transform incorrect at t=0.5. Expected: " + expectedRoot45 + " but got: " + animComp.boneTransforms[0]);
    }

    @Test
    public void testHierarchy() {
        AnimationSystem system = new AnimationSystem();
        AnimationComponent animComp = new AnimationComponent();

        // Setup Geometry: Root -> Parent -> Child
        BedrockGeometry geometry = new BedrockGeometry();
        geometry.minecraft_geometry = new ArrayList<>();
        BedrockGeometry.GeometryData geoData = new BedrockGeometry.GeometryData();
        geoData.bones = new ArrayList<>();

        BedrockGeometry.Bone rootBone = new BedrockGeometry.Bone();
        rootBone.name = "root";
        rootBone.pivot = List.of(0f, 0f, 0f);
        geoData.bones.add(rootBone);

        BedrockGeometry.Bone parentBone = new BedrockGeometry.Bone();
        parentBone.name = "parent";
        parentBone.parent = "root";
        parentBone.pivot = List.of(0f, 10f, 0f);
        geoData.bones.add(parentBone);

        BedrockGeometry.Bone childBone = new BedrockGeometry.Bone();
        childBone.name = "child";
        childBone.parent = "parent";
        childBone.pivot = List.of(0f, 15f, 0f);
        geoData.bones.add(childBone);

        geometry.minecraft_geometry.add(geoData);
        animComp.geometry = geometry;

        animComp.boneIndexMap = new HashMap<>();
        animComp.boneIndexMap.put("root", 0);
        animComp.boneIndexMap.put("parent", 1);
        animComp.boneIndexMap.put("child", 2);

        // Setup Animation: Only parent rotates 90 deg on Y
        BedrockAnimations animations = new BedrockAnimations();
        animations.animations = new HashMap<>();
        BedrockAnimations.Animation animation = new BedrockAnimations.Animation();
        animation.animation_length = 1.0f;
        animation.bones = new HashMap<>();

        BedrockAnimations.BoneAnimation parentAnim = new BedrockAnimations.BoneAnimation();
        parentAnim.rotation = List.of(0f, 90f, 0f);
        animation.bones.put("parent", parentAnim);

        animations.animations.put("test_anim", animation);
        animComp.animations = animations;
        animComp.currentAnimation = "test_anim";
        animComp.playing = true;

        system.updateAnimation(animComp, 0);

        // Root should be Identity
        assertTrue(animComp.boneTransforms[0].equals(new Matrix4f(), 0.0001f));

        // Parent should be rotated 90 deg on Y around its pivot (0,10,0)
        Matrix4f expectedParent = new Matrix4f().translate(0, 10, 0).rotateY((float) Math.toRadians(90)).translate(0, -10, 0);
        assertTrue(animComp.boneTransforms[1].equals(expectedParent, 0.0001f));

        // Child should be Identity relative to Parent, so WorldChild = WorldParent * LocalChild
        // LocalChild = Identity (since it has no animation and its pivot 15,0,0 is handled by T(15)*T(-15))
        // Wait, LocalChild = T(15) * R(0) * T(-15) = Identity.
        // So WorldChild should be equal to WorldParent.
        assertTrue(animComp.boneTransforms[2].equals(expectedParent, 0.0001f));
    }

    @Test
    public void testTranslationAndScale() {
        AnimationSystem system = new AnimationSystem();
        AnimationComponent animComp = new AnimationComponent();

        // Setup Geometry
        BedrockGeometry geometry = new BedrockGeometry();
        geometry.minecraft_geometry = new ArrayList<>();
        BedrockGeometry.GeometryData geoData = new BedrockGeometry.GeometryData();
        geoData.bones = new ArrayList<>();

        BedrockGeometry.Bone rootBone = new BedrockGeometry.Bone();
        rootBone.name = "root";
        rootBone.pivot = List.of(0f, 0f, 0f);
        geoData.bones.add(rootBone);

        geometry.minecraft_geometry.add(geoData);
        animComp.geometry = geometry;

        animComp.boneIndexMap = new HashMap<>();
        animComp.boneIndexMap.put("root", 0);

        // Setup Animation: Translation and Scale
        BedrockAnimations animations = new BedrockAnimations();
        animations.animations = new HashMap<>();
        BedrockAnimations.Animation animation = new BedrockAnimations.Animation();
        animation.animation_length = 1.0f;
        animation.bones = new HashMap<>();

        BedrockAnimations.BoneAnimation rootAnim = new BedrockAnimations.BoneAnimation();
        rootAnim.position = List.of(1f, 2f, 3f);
        rootAnim.scale = List.of(2f, 2f, 2f);
        animation.bones.put("root", rootAnim);

        animations.animations.put("test_anim", animation);
        animComp.animations = animations;
        animComp.currentAnimation = "test_anim";
        animComp.playing = true;

        system.updateAnimation(animComp, 0);

        // Root transform = T(animPos) * T(pivot) * R * S * T(-pivot)
        // = T(1,2,3) * T(0,0,0) * R(0) * S(2,2,2) * T(0,0,0) = T(1,2,3) * S(2,2,2)
        Matrix4f expected = new Matrix4f().translate(1, 2, 3).scale(2);
        assertTrue(animComp.boneTransforms[0].equals(expected, 0.0001f));
    }
}
