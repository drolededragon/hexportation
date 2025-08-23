package dev.kineticcat.hexportation.api.transfer

/**
 * Cross-platform replacement for Fabric's Storage interface.
 * Provides identical API so existing code needs zero changes.
 */
interface Storage<T> {
    
    /**
     * Insert into this storage.
     */
    fun insert(resource: T, maxAmount: Long, transaction: Any?): Long
    
    /**
     * Simulate insertion into this storage.
     */
    fun simulateInsert(resource: T, maxAmount: Long, transaction: Any?): Long
    
    /**
     * Get an iterator over all non-empty storage views.
     */
    fun nonEmptyIterator(): Iterator<StorageView<T>>
    
    /**
     * Get an iterator over all storage views.
     */
    fun iterator(): Iterator<StorageView<T>>
    
    /**
     * Extract from this storage.
     */
    fun extract(resource: T, maxAmount: Long, transaction: Any?): Long
    
    /**
     * Simulate extraction from this storage.
     */
    fun simulateExtract(resource: T, maxAmount: Long, transaction: Any?): Long
}