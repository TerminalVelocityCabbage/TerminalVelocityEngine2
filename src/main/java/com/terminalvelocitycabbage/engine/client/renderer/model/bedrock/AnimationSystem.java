package com.terminalvelocitycabbage.engine.client.renderer.model.bedrock;

import com.terminalvelocitycabbage.engine.ecs.Manager;
import com.terminalvelocitycabbage.engine.ecs.System;
import com.terminalvelocitycabbage.engine.util.Easing;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A system that handles the interpolation and calculation of bone transformations for
 * Bedrock-style skeletal animations.
 */
public class AnimationSystem extends System {

    @Override
    public void update(Manager manager, float deltaTime) {
        var entities = manager.getEntitiesWith(AnimationComponent.class);
        for (var entity : entities) {
            var animComp = entity.getComponent(AnimationComponent.class);
            if (animComp.animations != null && animComp.geometry != null) {
                updateAnimation(animComp, deltaTime);
            }
        }
    }

    /**
     * Updates the animation state for a specific component.
     * Calculates local bone transforms based on keyframes and combines them into world space.
     * 
     * @param animComp The animation component to update
     * @param deltaTime Time elapsed since the last update
     */
    private void updateAnimation(AnimationComponent animComp, float deltaTime) {
        if (animComp.playing && animComp.currentAnimation != null) {
            animComp.currentTime += deltaTime;
            var animation = animComp.animations.animations.get(animComp.currentAnimation);
            if (animation != null) {
                if (animComp.currentTime > animation.animation_length) {
                    if (animation.loop) {
                        animComp.currentTime %= animation.animation_length;
                    } else {
                        animComp.currentTime = animation.animation_length;
                        animComp.playing = false;
                    }
                }
            }
        }

        var animation = animComp.currentAnimation != null ? animComp.animations.animations.get(animComp.currentAnimation) : null;

        // Initialize bone transforms if needed
        if (animComp.boneTransforms == null || animComp.boneTransforms.length != animComp.boneIndexMap.size()) {
            animComp.boneTransforms = new Matrix4f[animComp.boneIndexMap.size()];
            for (int i = 0; i < animComp.boneTransforms.length; i++) {
                animComp.boneTransforms[i] = new Matrix4f();
            }
        }

        var geoData = animComp.geometry.minecraft_geometry.get(0);
        
        // Temporarily store local transforms
        Matrix4f[] localTransforms = new Matrix4f[animComp.boneIndexMap.size()];
        for (int i = 0; i < geoData.bones.size(); i++) {
            var bone = geoData.bones.get(i);
            var boneAnim = animation.bones.get(bone.name);
            
            Matrix4f transform = new Matrix4f();
            
            // Start with pivot translation (to local space)
            float px = bone.pivot.get(0);
            float py = bone.pivot.get(1);
            float pz = bone.pivot.get(2);
            
            // Animation values
            Vector3f animPos = interpolateVector(boneAnim != null ? boneAnim.position : null, animComp.currentTime);
            Vector3f animRot = interpolateVector(boneAnim != null ? boneAnim.rotation : null, animComp.currentTime);
            Vector3f animScale = interpolateVector(boneAnim != null ? boneAnim.scale : null, animComp.currentTime, new Vector3f(1, 1, 1));

            // Bedrock rotation: rotation around pivot
            // Transform = ParentWorldTransform * T(pivot) * R(anim) * R(bind) * T(-pivot) * T(animPos)
            // If vertices are NOT relative to pivot:
            // BoneLocal = T(animPos) * T(pivot) * R(bind) * R(anim) * S(anim) * T(-pivot)

            transform.translate(animPos.x, animPos.y, animPos.z);
            transform.translate(px, py, pz);

            // Rotation is relative to bind pose
            float bx = bone.rotation != null ? bone.rotation.get(0) : 0;
            float by = bone.rotation != null ? bone.rotation.get(1) : 0;
            float bz = bone.rotation != null ? bone.rotation.get(2) : 0;

            // Bedrock uses degrees and specific order
            transform.rotateZYX((float) Math.toRadians(bz + animRot.z), (float) Math.toRadians(by - animRot.y), (float) Math.toRadians(bx - animRot.x));

            transform.scale(animScale);
            transform.translate(-px, -py, -pz);

            localTransforms[i] = transform;
        }

            // Combine into world space
            for (int i = 0; i < geoData.bones.size(); i++) {
                var bone = geoData.bones.get(i);
                Matrix4f worldTransform = new Matrix4f(localTransforms[i]);

                String parentName = bone.parent;
                while (parentName != null) {
                    Integer parentIdx = animComp.boneIndexMap.get(parentName);
                    if (parentIdx == null) break;
                    // Apply parent transform to the left: Parent * World
                    worldTransform = new Matrix4f(localTransforms[parentIdx]).mul(worldTransform);
                    parentName = geoData.bones.get(parentIdx).parent;
                }

                animComp.boneTransforms[i] = worldTransform;
            }
    }

    private Vector3f interpolateVector(Object data, float time) {
        return interpolateVector(data, time, new Vector3f(0, 0, 0));
    }

    private Vector3f interpolateVector(Object data, float time, Vector3f defaultValue) {
        if (data == null) return defaultValue;
        if (data instanceof List<?> list) {
            // Static value
            return new Vector3f(((Number)list.get(0)).floatValue(), ((Number)list.get(1)).floatValue(), ((Number)list.get(2)).floatValue());
        }
        if (data instanceof Map<?, ?> map) {
            // Keyframes
            List<Float> times = new ArrayList<>();
            Map<Float, Vector3f> values = new java.util.TreeMap<>();
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                float kfTime = Float.parseFloat(entry.getKey().toString());
                times.add(kfTime);
                Object val = entry.getValue();
                if (val instanceof List<?> listVal) {
                    values.put(kfTime, new Vector3f(((Number)listVal.get(0)).floatValue(), ((Number)listVal.get(1)).floatValue(), ((Number)listVal.get(2)).floatValue()));
                } else if (val instanceof Map<?, ?> kfMap) {
                    List<?> post = (List<?>) kfMap.get("post");
                    values.put(kfTime, new Vector3f(((Number)post.get(0)).floatValue(), ((Number)post.get(1)).floatValue(), ((Number)post.get(2)).floatValue()));
                }
            }
            times.sort(Float::compare);

            if (times.isEmpty()) return defaultValue;
            if (time <= times.get(0)) return values.get(times.get(0));
            if (time >= times.get(times.size() - 1)) return values.get(times.get(times.size() - 1));

            for (int i = 0; i < times.size() - 1; i++) {
                float t0 = times.get(i);
                float t1 = times.get(i + 1);
                if (time >= t0 && time <= t1) {
                    float progress = (time - t0) / (t1 - t0);
                    Vector3f v0 = values.get(t0);
                    Vector3f v1 = values.get(t1);
                    return new Vector3f(v0).lerp(v1, progress);
                }
            }
        }
        return defaultValue;
    }
}
