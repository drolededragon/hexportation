package dev.kineticcat.hexportation.forge.transfer

import dev.kineticcat.hexportation.api.transfer.ItemVariant
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

/**
 * Forge implementation using singleton injection pattern.
 */
class ItemVariantImpl : ItemVariant.Implementation() {
    
    companion object {
        @JvmField
        val INSTANCE = ItemVariantImpl()
    }
    
    override fun of(item: Item): ItemVariant = ItemStackWrapper(ItemStack(item))
    
    override fun blank(): ItemVariant = ItemStackWrapper(ItemStack.EMPTY)
    
    /**
     * Wrapper class that extends our ItemVariant abstract class using Forge's ItemStack.
     */
    class ItemStackWrapper(
        private val itemStack: ItemStack
    ) : ItemVariant() {
        
        override val item: Item get() = itemStack.item
        
        override fun toStack(): ItemStack = itemStack.copy()
        
        override fun toStack(count: Int): ItemStack = itemStack.copy().apply { setCount(count) }
        
        fun getItemStack() = itemStack
        
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ItemVariant) return false
            
            // Compare items - for Forge we only care about the Item type, not NBT for now
            return when (other) {
                is ItemStackWrapper -> itemStack.item == other.itemStack.item
                else -> item == other.item
            }
        }
        
        override fun hashCode(): Int = itemStack.item.hashCode()
    }
}