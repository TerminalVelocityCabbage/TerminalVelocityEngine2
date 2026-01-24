package com.terminalvelocitycabbage.engine.client.ui.data.configs;

import org.joml.Vector2f;

public record ClipElementConfig(boolean horizontal, boolean vertical, Vector2f childOffset) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean horizontal;
        private boolean vertical;
        private Vector2f childOffset;

        public Builder horizontal(boolean horizontal) {
            this.horizontal = horizontal;
            return this;
        }

        public Builder vertical(boolean vertical) {
            this.vertical = vertical;
            return this;
        }

        public Builder childOffset(Vector2f childOffset) {
            this.childOffset = childOffset;
            return this;
        }

        public ClipElementConfig build() {
            return new ClipElementConfig(horizontal, vertical, childOffset);
        }
    }
}
