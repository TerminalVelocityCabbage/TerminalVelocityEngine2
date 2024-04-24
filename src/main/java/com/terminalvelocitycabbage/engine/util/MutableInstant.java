package com.terminalvelocitycabbage.engine.util;

/**
 * A utility class to track and update a set instant in time, useful for calculating deltaTime and storing it for
 * later use.
 */
public class MutableInstant {

    long instant;

    private MutableInstant(long instant) {
        this.instant = instant;
    }

    /**
     * @return a new instance of {@link MutableInstant} with the current time
     */
    public static MutableInstant ofNow() {
        return new MutableInstant(System.currentTimeMillis());
    }

    /**
     * @return The currently stored instant in time
     */
    public long getTimeInMillis() {
        return instant;
    }

    /**
     * Updates the stored instant to the current time.
     */
    public void now() {
        this.instant = System.currentTimeMillis();
    }

    /**
     * @return The deltaTime between now and the stored instant
     */
    public long getDeltaTime() {
        return System.currentTimeMillis() - instant;
    }

}
