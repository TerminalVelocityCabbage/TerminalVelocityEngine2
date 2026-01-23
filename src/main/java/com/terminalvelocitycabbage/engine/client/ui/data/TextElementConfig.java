package com.terminalvelocitycabbage.engine.client.ui.data;

import com.terminalvelocitycabbage.engine.client.ui.TextAlignment;
import com.terminalvelocitycabbage.engine.client.ui.TextWrapMode;
import com.terminalvelocitycabbage.engine.registry.Identifier;
import org.joml.Vector4f;

public record TextElementConfig(Vector4f textColor, Identifier fontIdentifier, int fontSize, int letterSpacing, int lineHeight, TextWrapMode wrapMode, TextAlignment textAlignment) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Vector4f textColor;
        private Identifier fontIdentifier;
        private int fontSize;
        private int letterSpacing;
        private int lineHeight;
        private TextWrapMode wrapMode;
        private TextAlignment textAlignment;

        public Builder textColor(Vector4f textColor) {
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

        public Builder wrapMode(TextWrapMode wrapMode) {
            this.wrapMode = wrapMode;
            return this;
        }

        public Builder textAlignment(TextAlignment textAlignment) {
            this.textAlignment = textAlignment;
            return this;
        }

        public TextElementConfig build() {
            return new TextElementConfig(textColor, fontIdentifier, fontSize, letterSpacing, lineHeight, wrapMode, textAlignment);
        }
    }
}
