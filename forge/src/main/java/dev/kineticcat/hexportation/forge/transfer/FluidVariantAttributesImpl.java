package dev.kineticcat.hexportation.forge.transfer;

import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

/**
 * Forge implementation of cross-platform FluidVariantAttributes.
 * Provides fluid display names using Forge's FluidStack.getDisplayName().
 */
public class FluidVariantAttributesImpl {
    
    /**
     * Get the display name for a fluid variant.
     * For Forge, we extract the FluidStack from our FluidVariant wrapper
     * and use its getDisplayName() method.
     */
    public static Component getName(Object fluidVariant) {
        if (fluidVariant instanceof FluidVariantImpl.FluidStackWrapper wrapper) {
            FluidStack stack = wrapper.getFluidStack();
            return stack.getDisplayName();
        }
        
        // Fallback for unexpected types
        return Component.literal("Unknown Fluid");
    }
}