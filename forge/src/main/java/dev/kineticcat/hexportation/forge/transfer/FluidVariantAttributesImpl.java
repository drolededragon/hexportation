package dev.kineticcat.hexportation.forge.transfer;

import dev.kineticcat.hexportation.api.transfer.FluidVariantAttributes;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;

/**
 * Forge implementation using singleton injection pattern.
 */
public class FluidVariantAttributesImpl extends FluidVariantAttributes.Implementation {
    
    public static final FluidVariantAttributesImpl INSTANCE = new FluidVariantAttributesImpl();
    
    /**
     * Get the display name for a fluid variant.
     * For Forge, we extract the FluidStack from our FluidVariant wrapper
     * and use its getDisplayName() method.
     */
    @Override
    public Component getName(Object fluidVariant) {
        if (fluidVariant instanceof FluidVariantImpl.FluidStackWrapper wrapper) {
            FluidStack stack = wrapper.getFluidStack();
            return stack.getDisplayName();
        }
        
        // Fallback for unexpected types
        return Component.literal("Unknown Fluid");
    }
}