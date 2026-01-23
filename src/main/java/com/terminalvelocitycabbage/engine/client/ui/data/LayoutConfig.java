package com.terminalvelocitycabbage.engine.client.ui.data;

import com.terminalvelocitycabbage.engine.client.ui.LayoutDirection;

public record LayoutConfig(Sizing sizing, Padding padding, int childGap, ChildAlignment childAlignment, LayoutDirection layoutDirection) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Sizing sizing;
        private Padding padding;
        private int childGap;
        private ChildAlignment childAlignment;
        private LayoutDirection layoutDirection;

        public Builder sizing(Sizing sizing) {
            this.sizing = sizing;
            return this;
        }

        public Builder padding(Padding padding) {
            this.padding = padding;
            return this;
        }

        public Builder childGap(int childGap) {
            this.childGap = childGap;
            return this;
        }

        public Builder childAlignment(ChildAlignment childAlignment) {
            this.childAlignment = childAlignment;
            return this;
        }

        public Builder layoutDirection(LayoutDirection layoutDirection) {
            this.layoutDirection = layoutDirection;
            return this;
        }

        public LayoutConfig build() {
            return new LayoutConfig(sizing, padding, childGap, childAlignment, layoutDirection);
        }
    }
}
