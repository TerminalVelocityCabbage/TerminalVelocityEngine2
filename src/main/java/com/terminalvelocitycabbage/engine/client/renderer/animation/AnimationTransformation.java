package com.terminalvelocitycabbage.engine.client.renderer.animation;

import org.joml.Vector3f;

/**
 * @param position The target position for this endpoint of a keyframe
 * @param rotation The target rotation for this endpoint of a keyframe
 * @param scale The target scale for this endpoint of a keyframe
 */
public record AnimationTransformation(
    Vector3f position,
    Vector3f rotation,
    Vector3f scale
) { }
