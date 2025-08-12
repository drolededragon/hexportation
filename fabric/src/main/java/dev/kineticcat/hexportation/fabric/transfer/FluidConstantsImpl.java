package dev.kineticcat.hexportation.fabric.transfer;

/**
 * Fabric implementation that provides actual Fabric FluidConstants values.
 * This ensures we always use the real Fabric values, not magic numbers.
 */
public class FluidConstantsImpl {
    
    public static long getBucket() {
        return net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.BUCKET;
    }
    
    public static long getIngot() {
        return net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.INGOT;
    }
}