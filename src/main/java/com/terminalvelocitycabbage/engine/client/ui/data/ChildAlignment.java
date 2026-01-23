package com.terminalvelocitycabbage.engine.client.ui.data;

import com.terminalvelocitycabbage.engine.client.ui.LayoutAlignmentX;
import com.terminalvelocitycabbage.engine.client.ui.LayoutAlignmentY;

public record ChildAlignment(LayoutAlignmentX x, LayoutAlignmentY y) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private LayoutAlignmentX x;
        private LayoutAlignmentY y;

        public Builder x(LayoutAlignmentX x) {
            this.x = x;
            return this;
        }

        public Builder y(LayoutAlignmentY y) {
            this.y = y;
            return this;
        }

        public ChildAlignment build() {
            return new ChildAlignment(x, y);
        }
    }
}
