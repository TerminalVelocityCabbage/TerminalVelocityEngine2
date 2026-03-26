package com.terminalvelocitycabbage.engine.client.renderer.model;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import java.util.Map;
import java.util.Optional;

public record Skeleton(
    Map<String, SkeletonBone> bones,
    Map<String, Integer> boneIndices,
    Matrix4f[] bindPoseMatrices
) {
    public record SkeletonBone(
        String name,
        Optional<String> parent,
        Vector3f position,
        Vector3f offset,
        Vector3f rotation
    ) { }
}
