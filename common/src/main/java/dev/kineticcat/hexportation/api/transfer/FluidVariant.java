package dev.kineticcat.hexportation.api.transfer;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;

/**
 * Cross-platform replacement for Fabric's FluidVariant.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
 * to dev.kineticcat.hexportation.api.transfer.FluidVariant
 */
public interface FluidVariant {
    
    /**
     * Create a FluidVariant from a Fluid.
     * This method signature matches Fabric's FluidVariant.of(Fluid)
     */
    @ExpectPlatform
    static FluidVariant of(Fluid fluid) {
        throw new AssertionError("Platform implementation required");
    }
    
    /**
     * Create a FluidVariant from a Fluid with NBT data.
     * This method signature matches Fabric's FluidVariant.of(Fluid, CompoundTag)
     */
    @ExpectPlatform
    static FluidVariant of(Fluid fluid, CompoundTag nbt) {
        throw new AssertionError("Platform implementation required");
    }
    
    /**
     * Get the fluid from this variant.
     */
    Fluid getFluid();
    
    /**
     * Get the NBT data from this variant.
     */
    CompoundTag getNbt();
}