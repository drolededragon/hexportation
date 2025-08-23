package dev.kineticcat.hexportation.forge.transfer

import dev.kineticcat.hexportation.api.transfer.FluidVariant
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack

/**
 * Forge implementation using singleton injection pattern.
 */
class FluidVariantImpl : FluidVariant.Implementation() {
    
    companion object {
        @JvmField
        val INSTANCE = FluidVariantImpl()
        
        @JvmStatic
        fun fromFluidStack(fluidStack: FluidStack): FluidVariant = FluidStackWrapper(fluidStack)
    }
    
    override fun of(fluid: Fluid): FluidVariant = 
        FluidStackWrapper(FluidStack(fluid, 1000)) // 1000 = 1 bucket in Forge
    
    override fun of(fluid: Fluid, nbt: CompoundTag?): FluidVariant {
        val fluidStack = FluidStack(fluid, 1000)
        nbt?.let { fluidStack.tag = it }
        return FluidStackWrapper(fluidStack)
    }
    
    /**
     * Wrapper class that extends our FluidVariant abstract class using Forge's FluidStack.
     */
    class FluidStackWrapper(private val fluidStack: FluidStack) : FluidVariant() {
        
        override val fluid: Fluid get() = fluidStack.fluid
        
        override val nbt: CompoundTag? get() = fluidStack.tag
        
        fun getFluidStack() = fluidStack
    }
}