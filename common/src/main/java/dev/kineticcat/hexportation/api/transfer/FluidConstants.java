package dev.kineticcat.hexportation.api.transfer;

import dev.architectury.injectables.annotations.ExpectPlatform;

/**
 * Cross-platform replacement for Fabric's FluidConstants.
 * Provides identical constants so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
 * to dev.kineticcat.hexportation.api.transfer.FluidConstants
 * 
 * Delegates to actual platform constants to avoid magic numbers and ensure synchronization.
 */
public class FluidConstants {
    
    @ExpectPlatform
    private static long getBucket() {
        throw new AssertionError("Platform implementation required");
    }
    
    @ExpectPlatform 
    private static long getIngot() {
        throw new AssertionError("Platform implementation required");
    }
    
    /**
     * A bucket's worth of fluid, in droplets.
     * Delegates to platform-specific FluidConstants.BUCKET value.
     */
    public static final long BUCKET = getBucket();
    
    /**
     * An ingot's worth of fluid, in droplets.
     * Delegates to platform-specific FluidConstants.INGOT value.
     */
    public static final long INGOT = getIngot();
}