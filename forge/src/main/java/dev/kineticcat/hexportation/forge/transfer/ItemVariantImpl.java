package dev.kineticcat.hexportation.forge.transfer;

import dev.kineticcat.hexportation.api.transfer.ItemVariant;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Forge implementation using singleton injection pattern.
 */
public class ItemVariantImpl extends ItemVariant.Implementation {
    
    public static final ItemVariantImpl INSTANCE = new ItemVariantImpl();
    
    @Override
    public ItemVariant of(Item item) {
        return new ItemStackWrapper(new ItemStack(item));
    }
    
    @Override
    public ItemVariant blank() {
        return new ItemStackWrapper(ItemStack.EMPTY);
    }
    
    /**
     * Wrapper class that extends our ItemVariant abstract class using Forge's ItemStack.
     */
    public static class ItemStackWrapper extends ItemVariant {
        private final ItemStack itemStack;
        
        public ItemStackWrapper(ItemStack itemStack) {
            this.itemStack = itemStack;
        }
        
        @Override
        public Item getItem() {
            return itemStack.getItem();
        }
        
        @Override
        public ItemStack toStack() {
            return itemStack.copy();
        }
        
        @Override
        public ItemStack toStack(int count) {
            ItemStack copy = itemStack.copy();
            copy.setCount(count);
            return copy;
        }
        
        public ItemStack getItemStack() {
            return itemStack;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof ItemVariant)) return false;
            
            // Compare items - for Forge we only care about the Item type, not NBT for now
            if (obj instanceof ItemStackWrapper other) {
                return this.itemStack.getItem() == other.itemStack.getItem();
            }
            
            // Generic ItemVariant comparison
            ItemVariant other = (ItemVariant) obj;
            return this.getItem() == other.getItem();
        }
        
        @Override
        public int hashCode() {
            return itemStack.getItem().hashCode();
        }
    }
}