package com.terminalvelocitycabbage.engine.debug;

import org.fusesource.jansi.Ansi;

public enum LogLevel {

    INFO("info", "[INFO]", Ansi.Color.BLUE),
    WARN("warn", "[WARN]", Ansi.Color.YELLOW),
    ERROR("error", "[ERROR]", Ansi.Color.RED),
    CRASH("crash", "[CRASH]", Ansi.Color.RED),
    DEBUG("debug", "[DEBUG]", Ansi.Color.GREEN);

    public String name;
    public String prefix;
    public Ansi.Color color;

    LogLevel(String name, String prefix, Ansi.Color color) {
        this.name = name;
        this.prefix = prefix;
        this.color = color;
    }

}
