package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.FluidVariantAttributes;
import dev.kineticcat.hexportation.fabric.transfer.FluidVariantImpl;
import net.minecraft.network.chat.Component;

/**
 * Fabric implementation using singleton injection pattern.
 */
public class FluidVariantAttributesImpl extends FluidVariantAttributes.Implementation {
    
    public static final FluidVariantAttributesImpl INSTANCE = new FluidVariantAttributesImpl();
    
    @Override
    public Component getName(Object fluidVariant) {
        // Extract the Fabric variant from our wrapper
        if (fluidVariant instanceof FluidVariantImpl.FabricFluidVariantWrapper wrapper) {
            return net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes.getName(
                wrapper.getFabricVariant()
            );
        }
        
        // Fallback for direct Fabric variant (shouldn't happen but be safe)
        return net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes.getName(
            (net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant) fluidVariant
        );
    }
}