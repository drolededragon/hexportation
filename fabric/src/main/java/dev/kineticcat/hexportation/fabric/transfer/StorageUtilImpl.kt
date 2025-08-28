package dev.kineticcat.hexportation.fabric.transfer

import dev.kineticcat.hexportation.api.transfer.Storage
import dev.kineticcat.hexportation.api.transfer.StorageView
import dev.kineticcat.hexportation.api.transfer.Transaction
import dev.kineticcat.hexportation.api.transfer.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.storage.Storage as FabricStorage
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView as FabricStorageView
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant as FabricItemVariant
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant as FabricFluidVariant
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction as FabricTransaction
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil as FabricStorageUtil
import java.util.function.Predicate

/**
 * Fabric implementation of our cross-platform StorageUtil.
 * Delegates to Fabric's actual StorageUtil.move() method.
 */
class StorageUtilImpl : StorageUtil.StorageUtilImpl() {
    
    companion object {
        @JvmField
        val INSTANCE = StorageUtilImpl()
    }
    
    /**
     * Implementation of StorageUtil.move() that delegates to Fabric's StorageUtil.
     * This preserves the exact same behavior as the original code.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> move(
        source: Storage<T>, 
        sink: Storage<T>, 
        filter: Predicate<T>, 
        maxAmount: Long, 
        transaction: Transaction?
    ): Long {
        // Extract Fabric storage from our wrappers
        val fabricSource: FabricStorage<T>? = when (source) {
            is ItemStorageImpl.FabricItemStorageWrapper -> 
                source.getFabricStorage() as FabricStorage<T>
            is FluidStorageImpl.FabricFluidStorageWrapper -> 
                source.getFabricStorage() as FabricStorage<T>
            else -> null
        }
        
        val fabricSink: FabricStorage<T>? = when (sink) {
            is ItemStorageImpl.FabricItemStorageWrapper -> 
                sink.getFabricStorage() as FabricStorage<T>
            is FluidStorageImpl.FabricFluidStorageWrapper -> 
                sink.getFabricStorage() as FabricStorage<T>
            else -> null
        }
        
        // Create a wrapper filter that converts Fabric variants to our abstractions
        val wrappedFilter = Predicate<T> { fabricVariant ->
            val ourVariant: T = when (fabricVariant) {
                is FabricItemVariant -> ItemVariantImpl.wrap(fabricVariant) as T
                is FabricFluidVariant -> FluidVariantImpl.wrap(fabricVariant) as T
                else -> fabricVariant // Fallback for other types
            }
            filter.test(ourVariant)
        }
        
        // Delegate to the real Fabric StorageUtil
        val fabricTransaction = (transaction as? TransactionImpl.FabricTransactionWrapper)?.getFabricTransaction()
        return FabricStorageUtil.move(
            fabricSource, 
            fabricSink, 
            wrappedFilter, 
            maxAmount, 
            fabricTransaction
        )
    }
    
    /**
     * Implementation of StorageUtil.simulateExtract() that delegates to Fabric's StorageUtil.
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T> simulateExtract(
        storageView: StorageView<T>, 
        resource: T, 
        maxAmount: Long, 
        transaction: Transaction?
    ): Long {
        // Extract Fabric StorageView from our wrapper
        val fabricStorageView: FabricStorageView<T>? = when (storageView) {
            is ItemStorageImpl.FabricItemStorageViewWrapper -> 
                storageView.getFabricStorageView() as FabricStorageView<T>
            is FluidStorageImpl.FabricFluidStorageViewWrapper -> 
                storageView.getFabricStorageView() as FabricStorageView<T>
            else -> null
        }
        
        // Convert our abstraction back to Fabric variant
        val fabricResource: T = when (resource) {
            is ItemVariantImpl.FabricItemVariantWrapper -> resource.getFabricVariant() as T
            is FluidVariantImpl.FabricFluidVariantWrapper -> resource.getFabricVariant() as T
            else -> resource
        }
        
        val fabricTransaction = (transaction as? TransactionImpl.FabricTransactionWrapper)?.getFabricTransaction()
        return FabricStorageUtil.simulateExtract(
            fabricStorageView,
            fabricResource,
            maxAmount,
            fabricTransaction
        )
    }
}