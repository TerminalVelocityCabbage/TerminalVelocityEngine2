package com.terminalvelocitycabbage.engine.client.ui.data.configs;

import com.terminalvelocitycabbage.engine.client.ui.data.BorderWidth;
import com.terminalvelocitycabbage.engine.util.Color;

public record BorderElementConfig(Color color, BorderWidth width) {

    public BorderElementConfig {
        if (color == null) color = new Color(0, 0, 0, 255);
        if (width == null) width = new BorderWidth();
    }
}
