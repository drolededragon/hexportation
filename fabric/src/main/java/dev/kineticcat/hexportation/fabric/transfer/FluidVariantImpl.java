package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.FluidVariant;
import net.minecraft.world.level.material.Fluid;

/**
 * Fabric implementation that delegates to actual Fabric FluidVariant.of().
 */
public class FluidVariantImpl {
    
    public static FluidVariant of(Fluid fluid) {
        // Cast the Fabric FluidVariant to our interface - this works because both are interfaces
        return (FluidVariant) net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant.of(fluid);
    }
}