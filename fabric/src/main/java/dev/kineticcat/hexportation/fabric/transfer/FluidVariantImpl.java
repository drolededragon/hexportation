package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.FluidVariant;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;

/**
 * Fabric implementation using singleton injection pattern.
 */
public class FluidVariantImpl extends FluidVariant.Implementation {
    
    public static final FluidVariantImpl INSTANCE = new FluidVariantImpl();
    
    @Override
    public FluidVariant of(Fluid fluid) {
        net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant fabricVariant = 
            net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant.of(fluid);
        return new FabricFluidVariantWrapper(fabricVariant);
    }
    
    @Override
    public FluidVariant of(Fluid fluid, CompoundTag nbt) {
        net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant fabricVariant = 
            net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant.of(fluid, nbt);
        return new FabricFluidVariantWrapper(fabricVariant);
    }
    
    
    /**
     * Wrap an existing Fabric FluidVariant into our wrapper.
     * Used by StorageView implementations.
     */
    public static FluidVariant wrap(net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant fabricVariant) {
        return new FabricFluidVariantWrapper(fabricVariant);
    }
    
    /**
     * Wrapper class that extends our FluidVariant abstract class using Fabric's FluidVariant.
     */
    public static class FabricFluidVariantWrapper extends FluidVariant {
        private final net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant fabricVariant;
        
        public FabricFluidVariantWrapper(net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant fabricVariant) {
            this.fabricVariant = fabricVariant;
        }
        
        @Override
        public Fluid getFluid() {
            return fabricVariant.getFluid();
        }
        
        @Override
        public CompoundTag getNbt() {
            return fabricVariant.getNbt();
        }
        
        public net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant getFabricVariant() {
            return fabricVariant;
        }
    }
}