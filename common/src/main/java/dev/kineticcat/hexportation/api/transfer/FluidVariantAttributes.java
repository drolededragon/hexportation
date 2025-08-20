package dev.kineticcat.hexportation.api.transfer;

import net.minecraft.network.chat.Component;

/**
 * Cross-platform replacement for Fabric's FluidVariantAttributes using singleton injection pattern.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes
 * to dev.kineticcat.hexportation.api.transfer.FluidVariantAttributes
 */
public class FluidVariantAttributes {
    
    private static Implementation implementation;
    
    public static Component getName(Object fluidVariant) {
        return implementation.getName(fluidVariant);
    }
    
    /**
     * Platform-specific implementation interface.
     */
    public static abstract class Implementation {
        public abstract Component getName(Object fluidVariant);
    }
    
    /**
     * Set the platform-specific implementation.
     * Called by platform modules during initialization.
     */
    public static void setImplementation(Implementation impl) {
        implementation = impl;
    }
}