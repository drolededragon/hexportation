package dev.kineticcat.hexportation.api.transfer

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.material.Fluid

/**
 * Cross-platform replacement for Fabric's FluidVariant using singleton injection pattern.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
 * to dev.kineticcat.hexportation.api.transfer.FluidVariant
 */
abstract class FluidVariant {
    
    /**
     * The fluid for this variant.
     */
    abstract val fluid: Fluid
    
    /**
     * The NBT data for this variant (null if no NBT).
     */
    abstract val nbt: CompoundTag?
    
    /**
     * Platform-specific implementation interface.
     */
    abstract class Implementation {
        abstract fun of(fluid: Fluid): FluidVariant
        abstract fun of(fluid: Fluid, nbt: CompoundTag?): FluidVariant
    }
    
    companion object {
        private var implementation: Implementation? = null
        
        /**
         * Create a FluidVariant from a Fluid.
         * This method signature matches Fabric's FluidVariant.of(Fluid)
         */
        @JvmStatic
        fun of(fluid: Fluid): FluidVariant {
            return implementation?.of(fluid) ?: throw IllegalStateException("FluidVariant implementation not set")
        }
        
        /**
         * Create a FluidVariant from a Fluid with NBT data.
         * This method signature matches Fabric's FluidVariant.of(Fluid, CompoundTag)
         */
        @JvmStatic
        fun of(fluid: Fluid, nbt: CompoundTag?): FluidVariant {
            return implementation?.of(fluid, nbt) ?: throw IllegalStateException("FluidVariant implementation not set")
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
}