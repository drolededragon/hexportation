package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.Storage;
import dev.kineticcat.hexportation.api.transfer.StorageView;
import dev.kineticcat.hexportation.api.transfer.Transaction;
import dev.kineticcat.hexportation.api.transfer.StorageUtil;

/**
 * Fabric implementation of our cross-platform StorageUtil.
 * Delegates to Fabric's actual StorageUtil.move() method.
 */
public class StorageUtilImpl extends StorageUtil.StorageUtilImpl {
    
    /**
     * Singleton instance for injection
     */
    public static final StorageUtilImpl INSTANCE = new StorageUtilImpl();
    
    /**
     * Implementation of StorageUtil.move() that delegates to Fabric's StorageUtil.
     * This preserves the exact same behavior as the original code.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> long move(Storage<T> source, Storage<T> sink, 
                               java.util.function.Predicate<T> filter, long maxAmount, Transaction transaction) {
        // Cast back to Fabric types and delegate to the real Fabric StorageUtil
        return net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil.move(
            (net.fabricmc.fabric.api.transfer.v1.storage.Storage<T>) source, 
            (net.fabricmc.fabric.api.transfer.v1.storage.Storage<T>) sink, 
            filter, 
            maxAmount, 
            (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
        );
    }
    
    /**
     * Implementation of StorageUtil.simulateExtract() that delegates to Fabric's StorageUtil.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> long simulateExtract(StorageView<T> storageView, T resource, long maxAmount, Transaction transaction) {
        return net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil.simulateExtract(
            (net.fabricmc.fabric.api.transfer.v1.storage.StorageView<T>) storageView,
            resource,
            maxAmount,
            (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
        );
    }
}