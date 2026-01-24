package com.terminalvelocitycabbage.engine.client.ui.data.configs;

import com.terminalvelocitycabbage.engine.client.ClientBase;
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
        private int parentId = -1;
        private FloatingAttachPoints attachPoints;
        private UI.PointerCaptureMode pointerCaptureMode;
        private UI.FloatingAttachToElement attachTo = UI.FloatingAttachToElement.PARENT;
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

        public Builder attachPoints(FloatingAttachPoints attachPoints) {
            this.attachPoints = attachPoints;
            return this;
        }

        public Builder pointerCaptureMode(UI.PointerCaptureMode pointerCaptureMode) {
            this.pointerCaptureMode = pointerCaptureMode;
            return this;
        }

        public Builder attachTo(UI.FloatingAttachToElement attachTo) {
            return switch (attachTo) {
                case PARENT -> {
                    this.attachTo = attachTo;
                    this.parentId = ClientBase.getInstance().getUIContext().getCurrentElement().parent().id();
                    yield this;
                }
                case ROOT -> {
                    this.attachTo = attachTo;
                    this.parentId = ClientBase.getInstance().getUIContext().getRootElement().id();
                    yield this;
                }
                case ELEMENT_WITH_ID -> throw new IllegalArgumentException("attachTo cannot be ELEMENT_WITH_ID when no element id set");
            };
        }

        public Builder attachTo(UI.FloatingAttachToElement attachTo, int elementId) {
            return switch (attachTo) {
                case ELEMENT_WITH_ID -> {
                    if (elementId == -1) {
                        throw new IllegalArgumentException("Invalid element id: " + elementId);
                    }
                    this.attachTo = attachTo;
                    this.parentId = elementId;
                    yield  this;
                }
                case ROOT, PARENT -> throw new IllegalArgumentException("Element id not required for " + attachTo + " attachment type");
            };
        }

        public Builder clipTo(UI.FloatingClipToElement clipTo) {
            this.clipTo = clipTo;
            return this;
        }

        public FloatingElementConfig build() {
            if (parentId == -1) {
                this.parentId = ClientBase.getInstance().getUIContext().getCurrentElement().parent().id();
            }
            return new FloatingElementConfig(offset, expand, zIndex, parentId, attachPoints, pointerCaptureMode, attachTo, clipTo);
        }
    }
}
