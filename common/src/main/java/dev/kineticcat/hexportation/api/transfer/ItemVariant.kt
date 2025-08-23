package dev.kineticcat.hexportation.api.transfer

import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

/**
 * Cross-platform replacement for Fabric's ItemVariant using singleton injection pattern.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
 * to dev.kineticcat.hexportation.api.transfer.ItemVariant
 */
abstract class ItemVariant {
    
    /**
     * Get the item from this variant.
     */
    abstract val item: Item
    
    /**
     * Convert this variant to an ItemStack.
     */
    abstract fun toStack(): ItemStack
    
    /**
     * Convert this variant to an ItemStack with a specific count.
     */
    abstract fun toStack(count: Int): ItemStack
    
    /**
     * Platform-specific implementation interface.
     */
    abstract class Implementation {
        abstract fun of(item: Item): ItemVariant
        abstract fun blank(): ItemVariant
    }
    
    companion object {
        private var implementation: Implementation? = null
        
        /**
         * Create an ItemVariant from an Item.
         * This method signature matches Fabric's ItemVariant.of(Item)
         */
        @JvmStatic
        fun of(item: Item): ItemVariant {
            return implementation?.of(item) ?: throw IllegalStateException("ItemVariant implementation not set")
        }
        
        /**
         * Create a blank (empty) ItemVariant.
         * This method signature matches Fabric's ItemVariant.blank()
         * Used for representing empty slots and comparisons.
         */
        @JvmStatic
        fun blank(): ItemVariant {
            return implementation?.blank() ?: throw IllegalStateException("ItemVariant implementation not set")
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