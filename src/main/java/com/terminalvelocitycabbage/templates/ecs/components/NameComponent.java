package com.terminalvelocitycabbage.templates.ecs.components;

import com.terminalvelocitycabbage.editor.hints.EditorHint;
import com.terminalvelocitycabbage.engine.ecs.Component;

@EditorHint.ComponentName(name = "Name")
public class NameComponent implements Component {

    String name;

    @Override
    public void parseComponentField(String field, String value) {
        if (field.equals("name")) {
            this.name = value;
        }
    }

    @Override
    public void setDefaults() {
        name = "Entity";
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
