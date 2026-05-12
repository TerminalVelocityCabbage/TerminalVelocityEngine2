package com.terminalvelocitycabbage.editor.registry;

import com.terminalvelocitycabbage.editor.Editor;
import com.terminalvelocitycabbage.templates.events.RoutineRegistrationEvent;

public class EditorRoutines {

    public static void init(RoutineRegistrationEvent event) {
        event.registerRoutineFromFile(Editor.ID, "editor_default");
    }

}
