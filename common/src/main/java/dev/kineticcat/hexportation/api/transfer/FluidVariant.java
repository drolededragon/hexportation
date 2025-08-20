package dev.kineticcat.hexportation.api.transfer;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;

/**
 * Cross-platform replacement for Fabric's FluidVariant using singleton injection pattern.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
 * to dev.kineticcat.hexportation.api.transfer.FluidVariant
 */
public abstract class FluidVariant {
    
    private static Implementation implementation;
    
    /**
     * Create a FluidVariant from a Fluid.
     * This method signature matches Fabric's FluidVariant.of(Fluid)
     */
    public static FluidVariant of(Fluid fluid) {
        return implementation.of(fluid);
    }
    
    /**
     * Create a FluidVariant from a Fluid with NBT data.
     * This method signature matches Fabric's FluidVariant.of(Fluid, CompoundTag)
     */
    public static FluidVariant of(Fluid fluid, CompoundTag nbt) {
        return implementation.of(fluid, nbt);
    }
    
    /**
     * Get the fluid from this variant.
     */
    public abstract Fluid getFluid();
    
    /**
     * Get the NBT data from this variant.
     */
    public abstract CompoundTag getNbt();
    
    /**
     * Platform-specific implementation interface.
     */
    public static abstract class Implementation {
        public abstract FluidVariant of(Fluid fluid);
        public abstract FluidVariant of(Fluid fluid, CompoundTag nbt);
    }
    
    /**
     * Set the platform-specific implementation.
     * Called by platform modules during initialization.
     */
    public static void setImplementation(Implementation impl) {
        implementation = impl;
    }
}