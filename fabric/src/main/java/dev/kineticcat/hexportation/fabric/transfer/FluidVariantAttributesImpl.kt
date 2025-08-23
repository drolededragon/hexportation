package dev.kineticcat.hexportation.fabric.transfer

import dev.kineticcat.hexportation.api.transfer.FluidVariantAttributes
import net.minecraft.network.chat.Component
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes as FabricFluidVariantAttributes
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant as FabricFluidVariant

/**
 * Fabric implementation using singleton injection pattern.
 */
class FluidVariantAttributesImpl : FluidVariantAttributes.Implementation() {
    
    companion object {
        @JvmField
        val INSTANCE = FluidVariantAttributesImpl()
    }
    
    override fun getName(fluidVariant: Any): Component {
        // Extract the Fabric variant from our wrapper
        return when (fluidVariant) {
            is FluidVariantImpl.FabricFluidVariantWrapper -> 
                FabricFluidVariantAttributes.getName(fluidVariant.getFabricVariant())
            
            // Fallback for direct Fabric variant (shouldn't happen but be safe)
            is FabricFluidVariant -> 
                FabricFluidVariantAttributes.getName(fluidVariant)
            
            else -> throw IllegalArgumentException("Unsupported fluid variant type: ${fluidVariant::class}")
        }
    }
}