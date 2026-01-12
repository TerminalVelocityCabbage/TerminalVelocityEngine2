package com.terminalvelocitycabbage.engine.scripting.api;

public enum ScriptVisibility {
    PRIVATE, //Script private - only this script has access to this function/const/etc.
    MODULE, //Module private - only the scripts in the defining script's module have access.
    MOD, //Mod private - only this mod has access to this function/const/etc.
    PUBLIC //Anyone can access
}

