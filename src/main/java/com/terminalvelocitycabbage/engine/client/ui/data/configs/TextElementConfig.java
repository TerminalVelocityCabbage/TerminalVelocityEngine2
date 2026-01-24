package com.terminalvelocitycabbage.engine.client.ui.data.configs;

import com.terminalvelocitycabbage.engine.client.ui.UI;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import com.terminalvelocitycabbage.engine.util.Color;

public record TextElementConfig(Color textColor, Identifier fontIdentifier, int fontSize, int letterSpacing, int lineHeight, UI.TextWrapMode wrapMode, UI.TextAlignment textAlignment) {

    public TextElementConfig {
        if (fontSize == 0) fontSize = 16;
        if (wrapMode == null) wrapMode = UI.TextWrapMode.NONE;
        if (textAlignment == null) textAlignment = UI.TextAlignment.LEFT;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Color textColor;
        private Identifier fontIdentifier;
        private int fontSize;
        private int letterSpacing;
        private int lineHeight;
        private UI.TextWrapMode wrapMode;
        private UI.TextAlignment textAlignment;

        public Builder textColor(Color textColor) {
            this.textColor = textColor;
            return this;
        }

        public Builder fontIdentifier(Identifier fontIdentifier) {
            this.fontIdentifier = fontIdentifier;
            return this;
        }

        public Builder fontSize(int fontSize) {
            this.fontSize = fontSize;
            return this;
        }

        public Builder letterSpacing(int letterSpacing) {
            this.letterSpacing = letterSpacing;
            return this;
        }

        public Builder lineHeight(int lineHeight) {
            this.lineHeight = lineHeight;
            return this;
        }

        public Builder wrapMode(UI.TextWrapMode wrapMode) {
            this.wrapMode = wrapMode;
            return this;
        }

        public Builder textAlignment(UI.TextAlignment textAlignment) {
            this.textAlignment = textAlignment;
            return this;
        }

        public TextElementConfig build() {
            return new TextElementConfig(textColor, fontIdentifier, fontSize, letterSpacing, lineHeight, wrapMode, textAlignment);
        }
    }
}
