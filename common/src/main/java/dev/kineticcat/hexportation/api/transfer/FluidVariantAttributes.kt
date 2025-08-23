package dev.kineticcat.hexportation.api.transfer

import net.minecraft.network.chat.Component

/**
 * Cross-platform replacement for Fabric's FluidVariantAttributes using singleton injection pattern.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes
 * to dev.kineticcat.hexportation.api.transfer.FluidVariantAttributes
 */
object FluidVariantAttributes {
    
    private var implementation: Implementation? = null
    
    @JvmStatic
    fun getName(fluidVariant: Any): Component {
        return implementation?.getName(fluidVariant) 
            ?: throw IllegalStateException("FluidVariantAttributes implementation not set")
    }
    
    /**
     * Platform-specific implementation interface.
     */
    abstract class Implementation {
        abstract fun getName(fluidVariant: Any): Component
    }
    
    /**
     * Set the platform-specific implementation.
     * Called by platform modules during initialization.
     */
    @JvmStatic
    fun setImplementation(impl: Implementation) {
        implementation = impl
    }
}