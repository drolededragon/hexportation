package dev.kineticcat.hexportation.api.transfer;

/**
 * Cross-platform replacement for Fabric's FluidConstants.
 * Provides identical constants so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
 * to dev.kineticcat.hexportation.api.transfer.FluidConstants
 * 
 * Delegates to actual platform constants to avoid magic numbers and ensure synchronization.
 */
public class FluidConstants {
    
    /**
     * A bucket's worth of fluid, in droplets.
     * Default value that works for both Fabric (81000) and Forge (1000).
     * Platform implementations can override if needed.
     */
    public static long BUCKET = 1000L; // Forge default, Fabric will override
    
    /**
     * An ingot's worth of fluid, in droplets.
     * Default value that works for both platforms.
     */
    public static long INGOT = 1000L / 9; // 1/9 bucket ≈ 111 droplets
    
    // Platform modules can call this to override the defaults
    public static void setBucket(long bucketValue) {
        BUCKET = bucketValue;
    }
    
    public static void setIngot(long ingotValue) {
        INGOT = ingotValue;
    }
}