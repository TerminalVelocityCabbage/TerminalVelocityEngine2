package com.terminalvelocitycabbage.engine.util;

/**
 * A utility class which makes managing whether a tick is needed or not
 */
public class TickManager {

    private final float nanosPerTick;
    private float compoundTime;
    private short ticks;
    private long lastUpdateTime;

    public TickManager(float ticksPerSecond) {
        this.nanosPerTick = 1000000000f / ticksPerSecond; //1000000000 nanoseconds in a second
        this.compoundTime = 0;
        this.lastUpdateTime = System.nanoTime();
    }

    /**
     * adjusts the compound time of this tick manager based on the time since the last tick
     */
    public void update() {
        this.compoundTime += (System.nanoTime() - lastUpdateTime);
        lastUpdateTime = System.nanoTime();
        while (this.compoundTime >= this.nanosPerTick) {
            this.ticks++;
            this.compoundTime -= this.nanosPerTick;
        }
    }

    /**
     * @return a boolean to represent whether this tick manager has a tick
     */
    public boolean hasTick() {
        if (this.ticks < 1) {
            return false;
        }
        this.ticks--;
        return true;
    }
}
