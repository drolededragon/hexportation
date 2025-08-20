package dev.kineticcat.hexportation.forge.transfer;

import dev.kineticcat.hexportation.api.transfer.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;

/**
 * Forge implementation using singleton injection pattern.
 */
public class FluidVariantImpl extends FluidVariant.Implementation {
    
    public static final FluidVariantImpl INSTANCE = new FluidVariantImpl();
    
    @Override
    public FluidVariant of(Fluid fluid) {
        return new FluidStackWrapper(new FluidStack(fluid, 1000)); // 1000 = 1 bucket in Forge
    }
    
    @Override
    public FluidVariant of(Fluid fluid, CompoundTag nbt) {
        FluidStack fluidStack = new FluidStack(fluid, 1000);
        if (nbt != null) {
            fluidStack.setTag(nbt);
        }
        return new FluidStackWrapper(fluidStack);
    }
    
    public static FluidVariant fromFluidStack(FluidStack fluidStack) {
        return new FluidStackWrapper(fluidStack);
    }
    
    /**
     * Wrapper class that extends our FluidVariant abstract class using Forge's FluidStack.
     */
    public static class FluidStackWrapper extends FluidVariant {
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