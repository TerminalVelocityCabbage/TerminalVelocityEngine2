package com.terminalvelocitycabbage.engine.client.ui.data.configs;

import com.terminalvelocitycabbage.engine.client.ui.data.BorderWidth;
import com.terminalvelocitycabbage.engine.util.Color;

public record BorderElementConfig(Color color, BorderWidth width) {

    public BorderElementConfig {
        if (color == null) color = new Color(0, 0, 0, 255);
        if (width == null) width = new BorderWidth();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Color color;
        private BorderWidth width;

        public Builder color(Color color) {
            this.color = color;
            return this;
        }

        public Builder width(BorderWidth width) {
            this.width = width;
            return this;
        }

        public BorderElementConfig build() {
            return new BorderElementConfig(color, width);
        }
    }
}
