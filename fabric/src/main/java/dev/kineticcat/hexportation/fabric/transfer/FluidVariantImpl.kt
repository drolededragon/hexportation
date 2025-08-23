package dev.kineticcat.hexportation.fabric.transfer

import dev.kineticcat.hexportation.api.transfer.FluidVariant
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.material.Fluid
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant as FabricFluidVariant

/**
 * Fabric implementation using singleton injection pattern.
 */
class FluidVariantImpl : FluidVariant.Implementation() {
    
    companion object {
        @JvmField
        val INSTANCE = FluidVariantImpl()
        
        /**
         * Wrap an existing Fabric FluidVariant into our wrapper.
         * Used by StorageView implementations.
         */
        @JvmStatic
        fun wrap(fabricVariant: FabricFluidVariant): FluidVariant = 
            FabricFluidVariantWrapper(fabricVariant)
    }
    
    override fun of(fluid: Fluid): FluidVariant = 
        FabricFluidVariantWrapper(FabricFluidVariant.of(fluid))
    
    override fun of(fluid: Fluid, nbt: CompoundTag?): FluidVariant = 
        FabricFluidVariantWrapper(FabricFluidVariant.of(fluid, nbt))
    
    /**
     * Wrapper class that extends our FluidVariant abstract class using Fabric's FluidVariant.
     */
    class FabricFluidVariantWrapper(
        private val fabricVariant: FabricFluidVariant
    ) : FluidVariant() {
        
        override val fluid: Fluid = fabricVariant.fluid
        
        override val nbt: CompoundTag? = fabricVariant.nbt
        
        fun getFabricVariant() = fabricVariant
    }
}