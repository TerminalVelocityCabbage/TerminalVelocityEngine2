package com.terminalvelocitycabbage.engine;

import com.terminalvelocitycabbage.engine.debug.Logger;
import com.terminalvelocitycabbage.engine.debug.LoggerSource;
import com.terminalvelocitycabbage.engine.registry.Identifier;

public abstract class Entrypoint implements LoggerSource {

    String namespace; //The namespace of this artifact
    Logger logger; //The logger for this artifact

    public Entrypoint(String namespace) {
        this.namespace = namespace;
        this.logger = new Logger(namespace);
    }

    /**
     * Returns the logger that sends messages to the console and reports for this client
     * @return this client's logger
     */
    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * Logic required when initializing this entrypoint
     */
    public abstract void init();

    /**
     * A place to clean up things initialized by your entrypoint
     */
    public abstract void destroy();

    /**
     * @return the namespace of this entrypoint
     */
    public String getNamespace() {
        return namespace;
    }

    public Identifier identifierOf(String name) {
        return new Identifier(getNamespace(), name);
    }

    public void preInit() {

    }
}
