package dev.kineticcat.hexportation.fabric.transfer;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;

/**
 * Fabric implementation of our cross-platform StorageUtil.
 * Delegates to Fabric's actual StorageUtil.move() method.
 */
public class StorageUtilImpl {
    
    /**
     * Implementation of StorageUtil.move() that delegates to Fabric's StorageUtil.
     * This preserves the exact same behavior as the original code.
     */
    @SuppressWarnings("unchecked")
    public static <T> long move(Object source, Object sink, 
                               java.util.function.Predicate<T> filter, long maxAmount, Object transaction) {
        // Cast back to Fabric types and delegate to the real Fabric StorageUtil
        return net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil.move(
            (Storage<T>) source, 
            (Storage<T>) sink, 
            filter, 
            maxAmount, 
            (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
        );
    }
}