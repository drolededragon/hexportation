package dev.kineticcat.hexportation.api.transfer;

import dev.architectury.injectables.annotations.ExpectPlatform;
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
}