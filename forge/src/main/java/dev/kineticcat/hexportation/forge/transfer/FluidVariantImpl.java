package dev.kineticcat.hexportation.forge.transfer;

import dev.kineticcat.hexportation.api.transfer.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Forge implementation that wraps FluidStack as FluidVariant.
 * Provides identical API to Fabric's FluidVariant through wrapper pattern.
 */
public class FluidVariantImpl {
    
    public static FluidVariant of(Fluid fluid) {
        return new FluidStackWrapper(new FluidStack(fluid, 1000)); // 1000 = 1 bucket in Forge
    }
    
    public static FluidVariant of(Fluid fluid, CompoundTag nbt) {
        FluidStack fluidStack = new FluidStack(fluid, 1000);
        if (nbt != null) {
            fluidStack.setTag(nbt);
        }
        return new FluidStackWrapper(fluidStack);
    }
    
    /**
     * Wrapper class that implements our FluidVariant interface using Forge's FluidStack.
     */
    public static class FluidStackWrapper implements FluidVariant {
        private final FluidStack fluidStack;
        
        public FluidStackWrapper(FluidStack fluidStack) {
            this.fluidStack = fluidStack;
        }
        
        @Override
        public Fluid getFluid() {
            return fluidStack.getFluid();
        }
        
        @Override
        public CompoundTag getNbt() {
            return fluidStack.getTag();
        }
        
        public FluidStack getFluidStack() {
            return fluidStack;
        }
    }
}