package dev.kineticcat.hexportation.api.transfer

/**
 * Cross-platform replacement for Fabric's StorageView interface.
 * Provides identical API so existing code needs zero changes.
 */
interface StorageView<T> {
    
    /**
     * Get the resource stored in this view.
     */
    val resource: T
    
    /**
     * Get the amount stored in this view.
     */
    val amount: Long
    
    /**
     * Get the capacity of this view.
     */
    val capacity: Long
    
    /**
     * Check if this view is blank (empty).
     */
    val isBlank: Boolean
    
    /**
     * Simulate extracting from this view.
     */
    fun simulateExtract(resource: T, maxAmount: Long, transaction: Any?): Long
    
    /**
     * Extract from this view.
     */
    fun extract(resource: T, maxAmount: Long, transaction: Any?): Long
}