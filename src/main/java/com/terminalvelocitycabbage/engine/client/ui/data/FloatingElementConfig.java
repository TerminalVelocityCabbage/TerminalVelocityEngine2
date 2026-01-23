package com.terminalvelocitycabbage.engine.client.ui.data;

import com.terminalvelocitycabbage.engine.client.ui.FloatingAttachToElement;
import com.terminalvelocitycabbage.engine.client.ui.FloatingClipToElement;
import com.terminalvelocitycabbage.engine.client.ui.PointerCaptureMode;
import org.joml.Vector2f;

public record FloatingElementConfig(
        Vector2f offset,
        Dimensions expand,
        int zIndex,
        int parentId,
        FloatingAttachPoints attachPoints,
        PointerCaptureMode pointerCaptureMode,
        FloatingAttachToElement attachTo,
        FloatingClipToElement clipTo
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Vector2f offset;
        private Dimensions expand;
        private int zIndex;
        private int parentId;
        private FloatingAttachPoints attachPoints;
        private PointerCaptureMode pointerCaptureMode;
        private FloatingAttachToElement attachTo;
        private FloatingClipToElement clipTo;

        public Builder offset(Vector2f offset) {
            this.offset = offset;
            return this;
        }

        public Builder expand(Dimensions expand) {
            this.expand = expand;
            return this;
        }

        public Builder zIndex(int zIndex) {
            this.zIndex = zIndex;
            return this;
        }

        public Builder parentId(int parentId) {
            this.parentId = parentId;
            return this;
        }

        public Builder attachPoints(FloatingAttachPoints attachPoints) {
            this.attachPoints = attachPoints;
            return this;
        }

        public Builder pointerCaptureMode(PointerCaptureMode pointerCaptureMode) {
            this.pointerCaptureMode = pointerCaptureMode;
            return this;
        }

        public Builder attachTo(FloatingAttachToElement attachTo) {
            this.attachTo = attachTo;
            return this;
        }

        public Builder clipTo(FloatingClipToElement clipTo) {
            this.clipTo = clipTo;
            return this;
        }

        public FloatingElementConfig build() {
            return new FloatingElementConfig(offset, expand, zIndex, parentId, attachPoints, pointerCaptureMode, attachTo, clipTo);
        }
    }
}
