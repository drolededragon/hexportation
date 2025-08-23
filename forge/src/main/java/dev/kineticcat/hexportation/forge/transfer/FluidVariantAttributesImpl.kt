package dev.kineticcat.hexportation.forge.transfer

import dev.kineticcat.hexportation.api.transfer.FluidVariantAttributes
import net.minecraft.network.chat.Component
import net.minecraftforge.fluids.FluidStack

/**
 * Forge implementation using singleton injection pattern.
 */
class FluidVariantAttributesImpl : FluidVariantAttributes.Implementation() {
    
    companion object {
        @JvmField
        val INSTANCE = FluidVariantAttributesImpl()
    }
    
    /**
     * Get the display name for a fluid variant.
     * For Forge, we extract the FluidStack from our FluidVariant wrapper
     * and use its getDisplayName() method.
     */
    override fun getName(fluidVariant: Any): Component = when (fluidVariant) {
        is FluidVariantImpl.FluidStackWrapper -> fluidVariant.getFluidStack().displayName
        else -> Component.literal("Unknown Fluid")
    }
}