package com.terminalvelocitycabbage.engine.util;

import org.joml.Vector2i;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;

public class BufferUtils {

    /**
     * Inserts the subimage into this atlas buffer
     * @param atlas The atlas image buffer which this image is being added to
     * @param subImage The image buffer to be inserted
     * @param dest The location for the sub image buffer
     */
    public static void insertSubImage(ByteBuffer atlas, int atlasW, int atlasH, ByteBuffer subImage, int subImageW, int subImageH, Vector2i dest) {

        long atlasBaseAddr = MemoryUtil.memAddress(atlas);
        long subImageBaseAddr = MemoryUtil.memAddress(subImage);

        for (int row = 0; row < subImageH; row++) {
            int destRow = dest.y() + row;
            if (destRow >= atlasH || dest.x() + subImageW > atlasW) continue;

            long destOffset = ((long) destRow * atlasW + dest.x()) * 4;
            long srcOffset = ((long) row * subImageW) * 4;

            MemoryUtil.memCopy(
                    subImageBaseAddr + srcOffset,
                    atlasBaseAddr + destOffset,
                    subImageW * 4L
            );
        }
    }

    public static Vector2i insertSubImage(ByteBuffer atlas, int atlasW, int atlasH, ByteBuffer subImage, int subImageW, int subImageH) {
        Vector2i location = new Vector2i();
        insertSubImage(atlas, atlasW, atlasH, subImage, subImageW, subImageH, location);
        return location;
    }

}
