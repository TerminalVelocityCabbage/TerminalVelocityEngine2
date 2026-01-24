package com.terminalvelocitycabbage.engine.client.ui.data.configs;

import com.terminalvelocitycabbage.engine.client.ui.UI;
import com.terminalvelocitycabbage.engine.client.ui.data.FloatingAttachPoints;
import org.joml.Vector2f;

public record FloatingElementConfig(
        Vector2f offset,
        Vector2f expand,
        int zIndex,
        int parentId,
        FloatingAttachPoints attachPoints,
        UI.PointerCaptureMode pointerCaptureMode,
        UI.FloatingAttachToElement attachTo,
        UI.FloatingClipToElement clipTo
) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Vector2f offset;
        private Vector2f expand;
        private int zIndex;
        private int parentId;
        private FloatingAttachPoints attachPoints;
        private UI.PointerCaptureMode pointerCaptureMode;
        private UI.FloatingAttachToElement attachTo;
        private UI.FloatingClipToElement clipTo;

        public Builder offset(Vector2f offset) {
            this.offset = offset;
            return this;
        }

        public Builder expand(Vector2f expand) {
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

        public Builder pointerCaptureMode(UI.PointerCaptureMode pointerCaptureMode) {
            this.pointerCaptureMode = pointerCaptureMode;
            return this;
        }

        public Builder attachTo(UI.FloatingAttachToElement attachTo) {
            this.attachTo = attachTo;
            return this;
        }

        public Builder clipTo(UI.FloatingClipToElement clipTo) {
            this.clipTo = clipTo;
            return this;
        }

        public FloatingElementConfig build() {
            return new FloatingElementConfig(offset, expand, zIndex, parentId, attachPoints, pointerCaptureMode, attachTo, clipTo);
        }
    }
}
