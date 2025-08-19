package dev.kineticcat.hexportation.api.transfer;

/**
 * Cross-platform replacement for Fabric's StorageUtil.
 * Provides identical method signatures so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
 * to dev.kineticcat.hexportation.api.transfer.StorageUtil
 */
public class StorageUtil {
    private static StorageUtilImpl implementation;
    
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
    public static <T> long move(Storage<T> source, Storage<T> sink, 
                               java.util.function.Predicate<T> filter, long maxAmount, Transaction transaction) {
        return implementation.move(source, sink, filter, maxAmount, transaction);
    }
    
    /**
     * Simulate extraction from a storage view.
     * Exact same signature as Fabric's StorageUtil.simulateExtract()
     */
    public static <T> long simulateExtract(StorageView<T> storageView, T resource, long maxAmount, Transaction transaction) {
        return implementation.simulateExtract(storageView, resource, maxAmount, transaction);
    }
    
    // Platform modules will call this to inject their implementation
    public static void setImplementation(StorageUtilImpl impl) {
        implementation = impl;
    }
    
    public static abstract class StorageUtilImpl {
        public abstract <T> long move(Storage<T> source, Storage<T> sink, 
                                     java.util.function.Predicate<T> filter, long maxAmount, Transaction transaction);
        
        public abstract <T> long simulateExtract(StorageView<T> storageView, T resource, long maxAmount, Transaction transaction);
    }
}