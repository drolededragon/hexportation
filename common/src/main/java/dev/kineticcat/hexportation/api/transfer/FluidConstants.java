package dev.kineticcat.hexportation.api.transfer;

/**
 * Cross-platform replacement for Fabric's FluidConstants.
 * Provides identical constants so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
 * to dev.kineticcat.hexportation.api.transfer.FluidConstants
 * 
 * We use the same values as Fabric since they're standard Minecraft fluid amounts.
 */
public class FluidConstants {
    
    /**
     * A bucket's worth of fluid, in droplets.
     * This matches Fabric's FluidConstants.BUCKET exactly.
     */
    public static final long BUCKET = 81000;
    
    /**
     * An ingot's worth of fluid, in droplets.
     * This matches Fabric's FluidConstants.INGOT exactly.
     */
    public static final long INGOT = 9000;
}