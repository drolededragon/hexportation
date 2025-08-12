package dev.kineticcat.hexportation.api.transfer;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.network.chat.Component;

/**
 * Cross-platform replacement for Fabric's FluidVariantAttributes.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes
 * to dev.kineticcat.hexportation.api.transfer.FluidVariantAttributes
 */
public class FluidVariantAttributes {
    
    @ExpectPlatform
    public static Component getName(Object fluidVariant) {
        throw new AssertionError("Platform implementation required");
    }
}