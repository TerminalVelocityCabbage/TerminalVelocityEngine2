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

        public boolean horizontal() {
            return horizontal;
        }

        public Builder horizontal(boolean horizontal) {
            this.horizontal = horizontal;
            return this;
        }

        public boolean vertical() {
            return vertical;
        }

        public Builder vertical(boolean vertical) {
            this.vertical = vertical;
            return this;
        }

        public Vector2f childOffset() {
            return childOffset;
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
