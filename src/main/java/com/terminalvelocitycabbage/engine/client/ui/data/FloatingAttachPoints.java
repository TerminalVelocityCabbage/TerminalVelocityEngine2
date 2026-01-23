package com.terminalvelocitycabbage.engine.client.ui.data;

import com.terminalvelocitycabbage.engine.client.ui.FloatingAttachPointType;

public record FloatingAttachPoints(FloatingAttachPointType element, FloatingAttachPointType parent) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private FloatingAttachPointType element;
        private FloatingAttachPointType parent;

        public Builder element(FloatingAttachPointType element) {
            this.element = element;
            return this;
        }

        public Builder parent(FloatingAttachPointType parent) {
            this.parent = parent;
            return this;
        }

        public FloatingAttachPoints build() {
            return new FloatingAttachPoints(element, parent);
        }
    }
}
