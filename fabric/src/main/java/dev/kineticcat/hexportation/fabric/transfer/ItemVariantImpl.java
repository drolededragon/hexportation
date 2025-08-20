package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.ItemVariant;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Fabric implementation using singleton injection pattern.
 */
public class ItemVariantImpl extends ItemVariant.Implementation {
    
    public static final ItemVariantImpl INSTANCE = new ItemVariantImpl();
    
    @Override
    public ItemVariant of(Item item) {
        net.fabricmc.fabric.api.transfer.v1.item.ItemVariant fabricVariant = 
            net.fabricmc.fabric.api.transfer.v1.item.ItemVariant.of(item);
        return new FabricItemVariantWrapper(fabricVariant);
    }
    
    @Override
    public ItemVariant blank() {
        net.fabricmc.fabric.api.transfer.v1.item.ItemVariant fabricVariant = 
            net.fabricmc.fabric.api.transfer.v1.item.ItemVariant.blank();
        return new FabricItemVariantWrapper(fabricVariant);
    }
    
    
    /**
     * Wrap an existing Fabric ItemVariant into our wrapper.
     * Used by StorageView implementations.
     */
    public static ItemVariant wrap(net.fabricmc.fabric.api.transfer.v1.item.ItemVariant fabricVariant) {
        return new FabricItemVariantWrapper(fabricVariant);
    }
    
    /**
     * Wrapper class that extends our ItemVariant abstract class using Fabric's ItemVariant.
     */
    public static class FabricItemVariantWrapper extends ItemVariant {
        private final net.fabricmc.fabric.api.transfer.v1.item.ItemVariant fabricVariant;
        
        public FabricItemVariantWrapper(net.fabricmc.fabric.api.transfer.v1.item.ItemVariant fabricVariant) {
            this.fabricVariant = fabricVariant;
        }
        
        @Override
        public Item getItem() {
            return fabricVariant.getItem();
        }
        
        @Override
        public ItemStack toStack() {
            return fabricVariant.toStack();
        }
        
        @Override
        public ItemStack toStack(int count) {
            return fabricVariant.toStack(count);
        }
        
        public net.fabricmc.fabric.api.transfer.v1.item.ItemVariant getFabricVariant() {
            return fabricVariant;
        }
    }
}