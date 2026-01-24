package com.terminalvelocitycabbage.engine.client.ui.data.configs;

import com.terminalvelocitycabbage.engine.client.ui.UI;
import com.terminalvelocitycabbage.engine.client.ui.data.ChildAlignment;
import com.terminalvelocitycabbage.engine.client.ui.data.Padding;
import com.terminalvelocitycabbage.engine.client.ui.data.Sizing;

public record LayoutConfig(Sizing sizing, Padding padding, int childGap, ChildAlignment childAlignment, UI.LayoutDirection layoutDirection, boolean wrap) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Sizing sizing;
        private Padding padding;
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
            return new LayoutConfig(sizing, padding, childGap, childAlignment, layoutDirection, wrap);
        }
    }
}
