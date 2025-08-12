package dev.kineticcat.hexportation.fabric.transfer;

import net.minecraft.network.chat.Component;

/**
 * Fabric implementation that delegates to actual Fabric FluidVariantAttributes.
 */
public class FluidVariantAttributesImpl {
    
    public static Component getName(Object fluidVariant) {
        return net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes.getName(
            (net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant) fluidVariant
        );
    }
}