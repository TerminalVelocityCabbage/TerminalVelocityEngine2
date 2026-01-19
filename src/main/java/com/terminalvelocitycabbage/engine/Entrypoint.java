package com.terminalvelocitycabbage.engine;

import com.terminalvelocitycabbage.engine.debug.Logger;
import com.terminalvelocitycabbage.engine.debug.LoggerSource;
import com.terminalvelocitycabbage.engine.registry.Identifier;

/**
 * The base class for all entrypoints in the engine.
 * An entrypoint is an artifact (like the game itself or a mod) that has a lifecycle
 * and its own namespace for resources and logging.
 */
public abstract class Entrypoint implements LoggerSource {

    /** The namespace of this artifact, used as a prefix for its resources. */
    String namespace;
    /** The logger for this artifact. */
    Logger logger;

    /**
     * Constructs a new Entrypoint with the specified namespace.
     * @param namespace The namespace of this artifact.
     */
    public Entrypoint(String namespace) {
        this.namespace = namespace;
        this.logger = new Logger(namespace);
    }

    /**
     * Returns the logger that sends messages to the console and reports for this entrypoint.
     * @return this entrypoint's logger.
     */
    @Override
    public Logger getLogger() {
        return logger;
    }

    /**
     * Logic required when initializing this entrypoint.
     * Called by the engine during the startup sequence.
     */
    public abstract void init();

    /**
     * A place to clean up things initialized by your entrypoint.
     * Called by the engine during the shutdown sequence.
     */
    public abstract void destroy();

    /**
     * @return the namespace of this entrypoint.
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * Creates an {@link Identifier} within this entrypoint's namespace.
     * @param name The name of the resource or object.
     * @return A new Identifier with this entrypoint's namespace and the given name.
     */
    public Identifier identifierOf(String name) {
        return new Identifier(getNamespace(), name);
    }
}
