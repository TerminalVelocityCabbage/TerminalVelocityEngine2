package com.terminalvelocitycabbage.engine.client.ui.data;

public record CustomElementConfig(Object customData) {

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Object customData;

        public Builder customData(Object customData) {
            this.customData = customData;
            return this;
        }

        public CustomElementConfig build() {
            return new CustomElementConfig(customData);
        }
    }
}
