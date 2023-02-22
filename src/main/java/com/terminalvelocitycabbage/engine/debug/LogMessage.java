package com.terminalvelocitycabbage.engine.debug;

import org.fusesource.jansi.AnsiConsole;

import java.util.Date;

import static org.fusesource.jansi.Ansi.ansi;

public class LogMessage {

    private LogLevel level;
    private String message;
    private boolean showNamespace;

    public LogMessage(LogLevel logLevel, String message, boolean showNamespace) {
        this.level = logLevel;
        this.message = message;
        this.showNamespace = showNamespace;
    }

    public LogMessage(LogLevel logLevel, String message) {
        this(logLevel, message, true);
    }

    public void printToConsole(Date date, String namespace, boolean showLevel, boolean showTimestamp) {
        AnsiConsole.out()
                .println(
                        ansi()
                                .fg(level.color)
                                .a(showTimestamp ? "[" + date.toString() + "]" : "")
                                .a(showLevel ? level.prefix + ": " : "")
                                .a(showNamespace ? "[" + namespace + "]" : "")
                                .reset()
                                .a(message)
                                .reset()
                );
    }

    public void errorToConsole(Date date, String namespace, boolean showLevel, boolean showTimestamp) {
        AnsiConsole.err()
                .println(
                        ansi()
                                .fg(level.color)
                                .a(showTimestamp ? "[" + date.toString() + "]" : "")
                                .a(showLevel ? level.prefix + ": " : "")
                                .a(showNamespace ? "[" + namespace + "]" : "")
                                .reset()
                                .a(message)
                                .reset()
                );
    }
}
