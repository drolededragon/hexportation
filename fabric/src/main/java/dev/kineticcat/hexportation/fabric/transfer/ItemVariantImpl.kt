package dev.kineticcat.hexportation.fabric.transfer

import dev.kineticcat.hexportation.api.transfer.ItemVariant
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant as FabricItemVariant

/**
 * Fabric implementation using singleton injection pattern.
 */
class ItemVariantImpl : ItemVariant.Implementation() {
    
    companion object {
        @JvmField
        val INSTANCE = ItemVariantImpl()
        
        /**
         * Wrap an existing Fabric ItemVariant into our wrapper.
         * Used by StorageView implementations.
         */
        @JvmStatic
        fun wrap(fabricVariant: FabricItemVariant): ItemVariant = 
            FabricItemVariantWrapper(fabricVariant)
    }
    
    override fun of(item: Item): ItemVariant = 
        FabricItemVariantWrapper(FabricItemVariant.of(item))
    
    override fun blank(): ItemVariant = 
        FabricItemVariantWrapper(FabricItemVariant.blank())
    
    /**
     * Wrapper class that extends our ItemVariant abstract class using Fabric's ItemVariant.
     */
    class FabricItemVariantWrapper(
        private val fabricVariant: FabricItemVariant
    ) : ItemVariant() {
        
        override val item: Item get() = fabricVariant.item
        
        override fun toStack(): ItemStack = fabricVariant.toStack()
        
        override fun toStack(count: Int): ItemStack = fabricVariant.toStack(count)
        
        fun getFabricVariant() = fabricVariant
    }
}