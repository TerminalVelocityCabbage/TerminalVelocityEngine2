package com.terminalvelocitycabbage.engine.client.ui.data.configs;

import com.terminalvelocitycabbage.engine.client.ui.UI;
import com.terminalvelocitycabbage.engine.client.ui.data.ChildAlignment;
import com.terminalvelocitycabbage.engine.client.ui.data.Margin;
import com.terminalvelocitycabbage.engine.client.ui.data.Padding;
import com.terminalvelocitycabbage.engine.client.ui.data.Sizing;
import com.terminalvelocitycabbage.engine.client.ui.data.SizingAxis;

public record LayoutConfig(Sizing sizing, Padding padding, Margin margin, int childGap, ChildAlignment childAlignment, UI.LayoutDirection layoutDirection, boolean wrap) {
    
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

        public Builder sizing(Sizing sizing) {
            this.sizing = sizing;
            return this;
        }

        public Builder padding(Padding padding) {
            this.padding = padding;
            return this;
        }

        public Builder margin(Margin margin) {
            this.margin = margin;
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

        public Builder layoutDirection(UI.LayoutDirection layoutDirection) {
            this.layoutDirection = layoutDirection;
            return this;
        }

        public Builder wrap(boolean wrap) {
            this.wrap = wrap;
            return this;
        }

        public LayoutConfig build() {
            return new LayoutConfig(sizing, padding, margin, childGap, childAlignment, layoutDirection, wrap);
        }
    }
}
