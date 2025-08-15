package dev.kineticcat.hexportation.forge.transfer;

/**
 * Forge implementation that provides fluid constants equivalent to Fabric's FluidConstants.
 * In Forge, 1 bucket = 1000 mB (millibuckets).
 */
public class FluidConstantsImpl {
    
    /**
     * Amount of millibuckets in one bucket.
     * Matches Fabric's FluidConstants.BUCKET value.
     */
    public static long getBucket() {
        return 1000L; // 1 bucket = 1000 mB in Forge
    }
}