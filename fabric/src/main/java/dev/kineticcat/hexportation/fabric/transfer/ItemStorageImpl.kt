package dev.kineticcat.hexportation.fabric.transfer

import dev.kineticcat.hexportation.api.transfer.Storage
import dev.kineticcat.hexportation.api.transfer.StorageView
import dev.kineticcat.hexportation.api.transfer.ItemVariant
import dev.kineticcat.hexportation.api.transfer.ItemStorage
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.fabricmc.fabric.api.transfer.v1.storage.Storage as FabricStorage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView as FabricStorageView
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant as FabricItemVariant
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction as FabricTransaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext as FabricTransactionContext
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil as FabricStorageUtil

/**
 * Fabric implementation that delegates to actual Fabric ItemStorage.SIDED.find().
 */
object ItemStorageImpl {
    
    /**
     * Singleton instance that implements our SidedStorage interface
     */
    @JvmField
    val INSTANCE = object : ItemStorage.SidedStorage() {
        override fun find(level: ServerLevel, pos: BlockPos, direction: Direction): Storage<ItemVariant>? {
            val fabricStorage = net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.find(level, pos, direction)
            return fabricStorage?.let { FabricItemStorageWrapper(it) }
        }
    }
    
    /**
     * Wrapper class that implements our Storage interface using Fabric's Storage.
     */
    class FabricItemStorageWrapper(
        private val fabricStorage: FabricStorage<FabricItemVariant>
    ) : Storage<ItemVariant> {
        
        override fun insert(resource: ItemVariant, maxAmount: Long, transaction: Any?): Long {
            val wrapper = resource as ItemVariantImpl.FabricItemVariantWrapper
            return fabricStorage.insert(
                wrapper.getFabricVariant(),
                maxAmount,
                transaction as? FabricTransaction
            )
        }
        
        override fun simulateInsert(resource: ItemVariant, maxAmount: Long, transaction: Any?): Long {
            val wrapper = resource as ItemVariantImpl.FabricItemVariantWrapper
            return fabricStorage.simulateInsert(
                wrapper.getFabricVariant(),
                maxAmount,
                transaction as? FabricTransaction
            )
        }
        
        override fun extract(resource: ItemVariant, maxAmount: Long, transaction: Any?): Long {
            val wrapper = resource as ItemVariantImpl.FabricItemVariantWrapper
            return fabricStorage.extract(
                wrapper.getFabricVariant(),
                maxAmount,
                transaction as? FabricTransaction
            )
        }
        
        override fun simulateExtract(resource: ItemVariant, maxAmount: Long, transaction: Any?): Long {
            val wrapper = resource as ItemVariantImpl.FabricItemVariantWrapper
            return fabricStorage.simulateExtract(
                wrapper.getFabricVariant(),
                maxAmount,
                transaction as? FabricTransaction
            )
        }
        
        override fun iterator(): Iterator<StorageView<ItemVariant>> =
            FabricStorageViewIterator(fabricStorage.iterator())
        
        override fun nonEmptyIterator(): Iterator<StorageView<ItemVariant>> =
            FabricStorageViewIterator(fabricStorage.nonEmptyIterator())
        
        fun getFabricStorage() = fabricStorage
    }
    
    /**
     * Iterator wrapper that converts Fabric StorageView to our StorageView.
     */
    private class FabricStorageViewIterator(
        private val fabricIterator: Iterator<out FabricStorageView<FabricItemVariant>>
    ) : Iterator<StorageView<ItemVariant>> {
        
        override fun hasNext(): Boolean = fabricIterator.hasNext()
        
        override fun next(): StorageView<ItemVariant> = 
            FabricItemStorageViewWrapper(fabricIterator.next())
    }
    
    /**
     * Wrapper class that implements our StorageView interface using Fabric's StorageView.
     */
    class FabricItemStorageViewWrapper(
        private val fabricStorageView: FabricStorageView<FabricItemVariant>
    ) : StorageView<ItemVariant> {
        
        override val resource: ItemVariant get() = 
            ItemVariantImpl.wrap(fabricStorageView.getResource())
        
        override val amount: Long get() = fabricStorageView.getAmount()
        
        override val capacity: Long get() = fabricStorageView.getCapacity()
        
        override val isBlank: Boolean get() = fabricStorageView.getResource().isBlank()
        
        override fun simulateExtract(resource: ItemVariant, maxAmount: Long, transaction: Any?): Long {
            val wrapper = resource as ItemVariantImpl.FabricItemVariantWrapper
            return FabricStorageUtil.simulateExtract(
                fabricStorageView,
                wrapper.getFabricVariant(),
                maxAmount,
                transaction as? FabricTransaction
            )
        }
        
        override fun extract(resource: ItemVariant, maxAmount: Long, transaction: Any?): Long {
            val wrapper = resource as ItemVariantImpl.FabricItemVariantWrapper
            return fabricStorageView.extract(
                wrapper.getFabricVariant(),
                maxAmount,
                transaction as? FabricTransactionContext
            )
        }
        
        fun getFabricStorageView() = fabricStorageView
    }
}