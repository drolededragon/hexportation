package dev.kineticcat.hexportation.fabric.transfer

import dev.kineticcat.hexportation.api.transfer.Storage
import dev.kineticcat.hexportation.api.transfer.StorageView
import dev.kineticcat.hexportation.api.transfer.FluidVariant
import dev.kineticcat.hexportation.api.transfer.FluidStorage
import dev.kineticcat.hexportation.api.transfer.FluidConstants
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.fabricmc.fabric.api.transfer.v1.storage.Storage as FabricStorage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView as FabricStorageView
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant as FabricFluidVariant
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction as FabricTransaction
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext as FabricTransactionContext
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil as FabricStorageUtil

/**
 * Fabric implementation that delegates to actual Fabric FluidStorage.SIDED.find().
 */
object FluidStorageImpl {
    
    /**
     * Singleton instance that implements our SidedStorage interface
     */
    @JvmField
    val INSTANCE = object : FluidStorage.SidedStorage() {
        override fun find(level: ServerLevel, pos: BlockPos, direction: Direction): Storage<FluidVariant>? {
            val fabricStorage = net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage.SIDED.find(level, pos, direction)
            return fabricStorage?.let { FabricFluidStorageWrapper(it) }
        }
    }
    
    /**
     * Wrapper class that implements our Storage interface using Fabric's Storage.
     */
    class FabricFluidStorageWrapper(
        private val fabricStorage: FabricStorage<FabricFluidVariant>
    ) : Storage<FluidVariant> {
        
        override fun insert(resource: FluidVariant, maxAmount: Long, transaction: Any?): Long {
            val wrapper = resource as FluidVariantImpl.FabricFluidVariantWrapper
            val fabricTransaction = (transaction as? TransactionImpl.FabricTransactionWrapper)?.getFabricTransaction()
            // Convert user millibuckets to Fabric droplets using established ratio
            val dropletsAmount = maxAmount * FluidConstants.BUCKET / 1000L
            val dropletsInserted = fabricStorage.insert(
                wrapper.getFabricVariant(),
                dropletsAmount,
                fabricTransaction
            )
            // Convert back to millibuckets for return value
            return dropletsInserted * 1000L / FluidConstants.BUCKET
        }
        
        override fun simulateInsert(resource: FluidVariant, maxAmount: Long, transaction: Any?): Long {
            val wrapper = resource as FluidVariantImpl.FabricFluidVariantWrapper
            val fabricTransaction = (transaction as? TransactionImpl.FabricTransactionWrapper)?.getFabricTransaction()
            // Convert user millibuckets to Fabric droplets using established ratio
            val dropletsAmount = maxAmount * FluidConstants.BUCKET / 1000L
            val dropletsSimulated = fabricStorage.simulateInsert(
                wrapper.getFabricVariant(),
                dropletsAmount,
                fabricTransaction
            )
            // Convert back to millibuckets for return value
            return dropletsSimulated * 1000L / FluidConstants.BUCKET
        }
        
        override fun extract(resource: FluidVariant, maxAmount: Long, transaction: Any?): Long {
            val wrapper = resource as FluidVariantImpl.FabricFluidVariantWrapper
            val fabricTransaction = (transaction as? TransactionImpl.FabricTransactionWrapper)?.getFabricTransaction()
            // Convert user millibuckets to Fabric droplets using established ratio
            val dropletsAmount = maxAmount * FluidConstants.BUCKET / 1000L
            val dropletsExtracted = fabricStorage.extract(
                wrapper.getFabricVariant(),
                dropletsAmount,
                fabricTransaction
            )
            // Convert back to millibuckets for return value
            return dropletsExtracted * 1000L / FluidConstants.BUCKET
        }
        
        override fun simulateExtract(resource: FluidVariant, maxAmount: Long, transaction: Any?): Long {
            val wrapper = resource as FluidVariantImpl.FabricFluidVariantWrapper
            val fabricTransaction = (transaction as? TransactionImpl.FabricTransactionWrapper)?.getFabricTransaction()
            // Convert user millibuckets to Fabric droplets using established ratio
            val dropletsAmount = maxAmount * FluidConstants.BUCKET / 1000L
            val dropletsSimulated = fabricStorage.simulateExtract(
                wrapper.getFabricVariant(),
                dropletsAmount,
                fabricTransaction
            )
            // Convert back to millibuckets for return value
            return dropletsSimulated * 1000L / FluidConstants.BUCKET
        }
        
        override fun iterator(): Iterator<StorageView<FluidVariant>> =
            FabricStorageViewIterator(fabricStorage.iterator())
        
        override fun nonEmptyIterator(): Iterator<StorageView<FluidVariant>> =
            FabricStorageViewIterator(fabricStorage.nonEmptyIterator())
        
        fun getFabricStorage() = fabricStorage
    }
    
    /**
     * Iterator wrapper that converts Fabric StorageView to our StorageView.
     */
    private class FabricStorageViewIterator(
        private val fabricIterator: Iterator<out FabricStorageView<FabricFluidVariant>>
    ) : Iterator<StorageView<FluidVariant>> {
        
        override fun hasNext(): Boolean = fabricIterator.hasNext()
        
        override fun next(): StorageView<FluidVariant> = 
            FabricFluidStorageViewWrapper(fabricIterator.next())
    }
    
    /**
     * Wrapper class that implements our StorageView interface using Fabric's StorageView.
     */
    class FabricFluidStorageViewWrapper(
        private val fabricStorageView: FabricStorageView<FabricFluidVariant>
    ) : StorageView<FluidVariant> {
        
        override val resource: FluidVariant get() = 
            FluidVariantImpl.wrap(fabricStorageView.getResource())
        
        override val amount: Long get() = fabricStorageView.getAmount() * 1000L / FluidConstants.BUCKET
        
        override val capacity: Long get() = fabricStorageView.getCapacity() * 1000L / FluidConstants.BUCKET
        
        override val isBlank: Boolean get() = fabricStorageView.getResource().isBlank()
        
        override fun simulateExtract(resource: FluidVariant, maxAmount: Long, transaction: Any?): Long {
            val wrapper = resource as FluidVariantImpl.FabricFluidVariantWrapper
            val fabricTransaction = (transaction as? TransactionImpl.FabricTransactionWrapper)?.getFabricTransaction()
            // Convert user millibuckets to Fabric droplets using established ratio
            val dropletsAmount = maxAmount * FluidConstants.BUCKET / 1000L
            val dropletsSimulated = FabricStorageUtil.simulateExtract(
                fabricStorageView,
                wrapper.getFabricVariant(),
                dropletsAmount,
                fabricTransaction
            )
            // Convert back to millibuckets for return value
            return dropletsSimulated * 1000L / FluidConstants.BUCKET
        }
        
        override fun extract(resource: FluidVariant, maxAmount: Long, transaction: Any?): Long {
            val wrapper = resource as FluidVariantImpl.FabricFluidVariantWrapper
            val fabricTransaction = (transaction as? TransactionImpl.FabricTransactionWrapper)?.getFabricTransaction()
            // Convert user millibuckets to Fabric droplets using established ratio
            val dropletsAmount = maxAmount * FluidConstants.BUCKET / 1000L
            val dropletsExtracted = fabricStorageView.extract(
                wrapper.getFabricVariant(),
                dropletsAmount,
                fabricTransaction
            )
            // Convert back to millibuckets for return value
            return dropletsExtracted * 1000L / FluidConstants.BUCKET
        }
        
        fun getFabricStorageView() = fabricStorageView
    }
}