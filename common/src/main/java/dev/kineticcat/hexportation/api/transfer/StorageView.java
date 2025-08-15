package dev.kineticcat.hexportation.api.transfer;

/**
 * Cross-platform replacement for Fabric's StorageView interface.
 * Provides identical API so existing code needs zero changes.
 */
public interface StorageView<T> {
    
    /**
     * Get the resource stored in this view.
     */
    T getResource();
    
    /**
     * Get the amount stored in this view.
     */
    long getAmount();
    
    /**
     * Get the capacity of this view.
     */
    long getCapacity();
    
    /**
     * Simulate extracting from this view.
     */
    long simulateExtract(T resource, long maxAmount, Object transaction);
    
    /**
     * Extract from this view.
     */
    long extract(T resource, long maxAmount, Object transaction);
}