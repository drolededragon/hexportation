package dev.kineticcat.hexportation.api.transfer

import java.util.function.Predicate

/**
 * Cross-platform replacement for Fabric's StorageUtil.
 * Provides identical method signatures so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
 * to dev.kineticcat.hexportation.api.transfer.StorageUtil
 */
object StorageUtil {
    private var implementation: StorageUtilImpl? = null
    
    /**
     * Move resources from source to sink storage.
     * Exact same signature as Fabric's StorageUtil.move()
     * 
     * @param source Source storage
     * @param sink Destination storage  
     * @param filter Filter predicate (usually { true } to accept all)
     * @param maxAmount Maximum amount to move
     * @param transaction Transaction object (usually null for auto-commit)
     * @return Amount actually moved
     */
    @JvmStatic
    fun <T> move(
        source: Storage<T>, 
        sink: Storage<T>, 
        filter: Predicate<T>, 
        maxAmount: Long, 
        transaction: Transaction?
    ): Long {
        return implementation?.move(source, sink, filter, maxAmount, transaction) 
            ?: throw IllegalStateException("StorageUtil implementation not set")
    }
    
    /**
     * Simulate extraction from a storage view.
     * Exact same signature as Fabric's StorageUtil.simulateExtract()
     */
    @JvmStatic
    fun <T> simulateExtract(
        storageView: StorageView<T>, 
        resource: T, 
        maxAmount: Long, 
        transaction: Transaction?
    ): Long {
        return implementation?.simulateExtract(storageView, resource, maxAmount, transaction) 
            ?: throw IllegalStateException("StorageUtil implementation not set")
    }
    
    // Platform modules will call this to inject their implementation
    @JvmStatic
    fun setImplementation(impl: StorageUtilImpl) {
        implementation = impl
    }
    
    abstract class StorageUtilImpl {
        abstract fun <T> move(
            source: Storage<T>, 
            sink: Storage<T>, 
            filter: Predicate<T>, 
            maxAmount: Long, 
            transaction: Transaction?
        ): Long
        
        abstract fun <T> simulateExtract(
            storageView: StorageView<T>, 
            resource: T, 
            maxAmount: Long, 
            transaction: Transaction?
        ): Long
    }
}