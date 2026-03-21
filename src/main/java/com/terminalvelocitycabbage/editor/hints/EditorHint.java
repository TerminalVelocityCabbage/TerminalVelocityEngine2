package com.terminalvelocitycabbage.editor.hints;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class EditorHint {

    @Retention(RetentionPolicy.RUNTIME)
    public @interface ComponentName {
        String name();
    }

}
