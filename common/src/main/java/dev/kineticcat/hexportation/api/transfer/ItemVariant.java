package dev.kineticcat.hexportation.api.transfer;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Cross-platform replacement for Fabric's ItemVariant using singleton injection pattern.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
 * to dev.kineticcat.hexportation.api.transfer.ItemVariant
 */
public abstract class ItemVariant {
    
    private static Implementation implementation;
    
    /**
     * Create an ItemVariant from an Item.
     * This method signature matches Fabric's ItemVariant.of(Item)
     */
    public static ItemVariant of(Item item) {
        return implementation.of(item);
    }
    
    /**
     * Create a blank (empty) ItemVariant.
     * This method signature matches Fabric's ItemVariant.blank()
     * Used for representing empty slots and comparisons.
     */
    public static ItemVariant blank() {
        return implementation.blank();
    }
    
    /**
     * Get the item from this variant.
     */
    public abstract Item getItem();
    
    /**
     * Convert this variant to an ItemStack.
     */
    public abstract ItemStack toStack();
    
    /**
     * Convert this variant to an ItemStack with a specific count.
     */
    public abstract ItemStack toStack(int count);
    
    /**
     * Platform-specific implementation interface.
     */
    public static abstract class Implementation {
        public abstract ItemVariant of(Item item);
        public abstract ItemVariant blank();
    }
    
    /**
     * Set the platform-specific implementation.
     * Called by platform modules during initialization.
     */
    public static void setImplementation(Implementation impl) {
        implementation = impl;
    }
}