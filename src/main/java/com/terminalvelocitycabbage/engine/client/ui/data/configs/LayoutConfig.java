package com.terminalvelocitycabbage.engine.client.ui.data.configs;

import com.terminalvelocitycabbage.engine.client.ui.UI;
import com.terminalvelocitycabbage.engine.client.ui.data.*;

public record LayoutConfig(Sizing sizing, Padding padding, Margin margin, int childGap, ChildAlignment childAlignment, UI.LayoutDirection layoutDirection, boolean wrap, float aspectRatio) {
    
    public LayoutConfig {
        if (sizing == null) sizing = new Sizing(SizingAxis.fit(), SizingAxis.fit());
        if (padding == null) padding = new Padding();
        if (margin == null) margin = new Margin();
        if (childAlignment == null) childAlignment = new ChildAlignment(UI.HorizontalAlignment.LEFT, UI.VerticalAlignment.TOP);
        if (layoutDirection == null) layoutDirection = UI.LayoutDirection.LEFT_TO_RIGHT;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Sizing sizing;
        private Padding padding;
        private Margin margin;
        private int childGap;
        private ChildAlignment childAlignment;
        private UI.LayoutDirection layoutDirection;
        private boolean wrap;
        private float aspectRatio;

        public Sizing sizing() {
            return sizing;
        }

        public Builder sizing(Sizing sizing) {
            this.sizing = sizing;
            return this;
        }

        public Padding padding() {
            return padding;
        }

        public Builder padding(Padding padding) {
            this.padding = padding;
            return this;
        }

        public Margin margin() {
            return margin;
        }

        public Builder margin(Margin margin) {
            this.margin = margin;
            return this;
        }

        public int childGap() {
            return childGap;
        }

        public Builder childGap(int childGap) {
            this.childGap = childGap;
            return this;
        }

        public ChildAlignment childAlignment() {
            return childAlignment;
        }

        public Builder childAlignment(ChildAlignment childAlignment) {
            this.childAlignment = childAlignment;
            return this;
        }

        public UI.LayoutDirection layoutDirection() {
            return layoutDirection;
        }

        public Builder layoutDirection(UI.LayoutDirection layoutDirection) {
            this.layoutDirection = layoutDirection;
            return this;
        }

        public boolean wrap() {
            return wrap;
        }

        public Builder wrap(boolean wrap) {
            this.wrap = wrap;
            return this;
        }

        public float aspectRatio() {
            return aspectRatio;
        }

        public Builder aspectRatio(float aspectRatio) {
            this.aspectRatio = aspectRatio;
            return this;
        }

        public LayoutConfig build() {
            return new LayoutConfig(sizing, padding, margin, childGap, childAlignment, layoutDirection, wrap, aspectRatio);
        }
    }
}
