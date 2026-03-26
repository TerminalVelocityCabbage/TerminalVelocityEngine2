package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.engine.client.renderer.lighting.DirectionalLight;
import com.terminalvelocitycabbage.engine.ecs.Component;
import com.terminalvelocitycabbage.engine.util.Color;
import org.joml.Vector3f;

public class DirectionalLightComponent implements Component {

    private DirectionalLight light;

    @Override
    public void setDefaults() {
        this.light = new DirectionalLight(new Vector3f(0, -1, 0), new Color(1.0f, 1.0f, 1.0f, 1.0f), 1.0f);
    }

    public DirectionalLight getLight() {
        return light;
    }

    public void setLight(DirectionalLight light) {
        this.light = light;
    }

    @Override
    public void parseComponentField(String field, String value) {
        if (field.equals("intensity")) {
            light.setIntensity(Float.parseFloat(value));
        } else if (field.equals("direction")) {
            String[] parts = value.substring(1, value.length() - 1).split(",");
            light.setDirection(new Vector3f(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2])).normalize());
        } else if (field.equals("color")) {
            String[] parts = value.substring(1, value.length() - 1).split(",");
            light.setColor(new Color(Float.parseFloat(parts[0]), Float.parseFloat(parts[1]), Float.parseFloat(parts[2]), Float.parseFloat(parts[3])));
        }
    }
}
