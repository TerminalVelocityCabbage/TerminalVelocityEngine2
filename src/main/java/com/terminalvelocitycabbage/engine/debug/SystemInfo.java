package com.terminalvelocitycabbage.engine.debug;

import java.lang.management.ManagementFactory;

public class SystemInfo {

    private static long cachedMaxMemory = -1;

    private static int cachedLogicalProcessors = -1;
    private static String cachedArchitecture = null;

    private static String cachedOSName = null;
    private static String cachedOSVersion = null;

    public static String gpuVendor;
    public static String gpuModel;
    public static String gpuVersion;

    public static int getAvailableProcessors() {
        if (cachedLogicalProcessors == -1) {
            cachedLogicalProcessors = Runtime.getRuntime().availableProcessors();
        }
        return cachedLogicalProcessors;
    }

    public static long getAllocatedMemory() {
        long max = Runtime.getRuntime().maxMemory();
        if (max > getMaxMemory()) {
            return -1;
        }
        return max;
    }

    public static long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }

    public static long getMaxMemory() {
        if (cachedMaxMemory == -1) {
            cachedMaxMemory = Runtime.getRuntime().totalMemory();
        }
        return cachedMaxMemory;
    }

    public static String getArchitecture() {
        if (cachedArchitecture == null) {
            cachedArchitecture = ManagementFactory.getOperatingSystemMXBean().getArch();
        }
        return cachedArchitecture;
    }

    public static String getOSName() {
        if (cachedOSName == null) {
            cachedOSName = ManagementFactory.getOperatingSystemMXBean().getName();
        }
        return cachedOSName;
    }

    public static String getOSVersion() {
        if (cachedOSVersion == null) {
            cachedOSVersion = ManagementFactory.getOperatingSystemMXBean().getVersion();
        }
        return cachedOSVersion;
    }

    public static String getGpuVendor() {
        if (gpuVendor == null) {
            Log.error("System Collection Error: GPU Vendor");
        }
        return gpuVendor;
    }

    public static String getGpuModel() {
        if (gpuModel == null) {
            Log.error("System Collection Error: GPU Model");
        }
        return gpuModel;
    }

    public static String getGpuVersion() {
        if (gpuVersion == null) {
            Log.error("System Collection Error: GPU Version");
        }
        return gpuVersion;
    }
}
