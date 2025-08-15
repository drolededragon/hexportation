package dev.kineticcat.hexportation.api.transfer;

import java.util.Iterator;

/**
 * Cross-platform replacement for Fabric's Storage interface.
 * Provides identical API so existing code needs zero changes.
 */
public interface Storage<T> {
    
    /**
     * Insert into this storage.
     */
    long insert(T resource, long maxAmount, Object transaction);
    
    /**
     * Simulate insertion into this storage.
     */
    long simulateInsert(T resource, long maxAmount, Object transaction);
    
    /**
     * Get an iterator over all non-empty storage views.
     */
    Iterator<StorageView<T>> nonEmptyIterator();
    
    /**
     * Get an iterator over all storage views.
     */
    Iterator<StorageView<T>> iterator();
    
    /**
     * Extract from this storage.
     */
    long extract(T resource, long maxAmount, Object transaction);
    
    /**
     * Simulate extraction from this storage.
     */
    long simulateExtract(T resource, long maxAmount, Object transaction);
}