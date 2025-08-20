package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.Storage;
import dev.kineticcat.hexportation.api.transfer.StorageView;
import dev.kineticcat.hexportation.api.transfer.Transaction;
import dev.kineticcat.hexportation.api.transfer.StorageUtil;
import dev.kineticcat.hexportation.fabric.transfer.ItemVariantImpl;
import dev.kineticcat.hexportation.fabric.transfer.FluidVariantImpl;

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
        // Extract Fabric storage from our wrappers
        net.fabricmc.fabric.api.transfer.v1.storage.Storage<T> fabricSource = null;
        net.fabricmc.fabric.api.transfer.v1.storage.Storage<T> fabricSink = null;
        
        // Handle ItemStorage wrappers
        if (source instanceof ItemStorageImpl.FabricItemStorageWrapper) {
            fabricSource = (net.fabricmc.fabric.api.transfer.v1.storage.Storage<T>) 
                ((ItemStorageImpl.FabricItemStorageWrapper) source).getFabricStorage();
        }
        if (sink instanceof ItemStorageImpl.FabricItemStorageWrapper) {
            fabricSink = (net.fabricmc.fabric.api.transfer.v1.storage.Storage<T>) 
                ((ItemStorageImpl.FabricItemStorageWrapper) sink).getFabricStorage();
        }
        
        // Handle FluidStorage wrappers
        if (source instanceof FluidStorageImpl.FabricFluidStorageWrapper) {
            fabricSource = (net.fabricmc.fabric.api.transfer.v1.storage.Storage<T>) 
                ((FluidStorageImpl.FabricFluidStorageWrapper) source).getFabricStorage();
        }
        if (sink instanceof FluidStorageImpl.FabricFluidStorageWrapper) {
            fabricSink = (net.fabricmc.fabric.api.transfer.v1.storage.Storage<T>) 
                ((FluidStorageImpl.FabricFluidStorageWrapper) sink).getFabricStorage();
        }
        
        // Create a wrapper filter that converts Fabric variants to our abstractions
        java.util.function.Predicate<T> wrappedFilter = fabricVariant -> {
            // Convert Fabric variant to our abstraction
            T ourVariant = null;
            
            // Handle ItemVariant conversion
            if (fabricVariant instanceof net.fabricmc.fabric.api.transfer.v1.item.ItemVariant) {
                ourVariant = (T) ItemVariantImpl.wrap((net.fabricmc.fabric.api.transfer.v1.item.ItemVariant) fabricVariant);
            }
            // Handle FluidVariant conversion  
            else if (fabricVariant instanceof net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant) {
                ourVariant = (T) FluidVariantImpl.wrap((net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant) fabricVariant);
            }
            else {
                ourVariant = fabricVariant; // Fallback for other types
            }
            
            return filter.test(ourVariant);
        };
        
        // Delegate to the real Fabric StorageUtil
        return net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil.move(
            fabricSource, 
            fabricSink, 
            wrappedFilter, 
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
        // Extract Fabric StorageView from our wrapper
        net.fabricmc.fabric.api.transfer.v1.storage.StorageView<T> fabricStorageView = null;
        
        // Handle ItemStorageView wrappers
        if (storageView instanceof ItemStorageImpl.FabricItemStorageViewWrapper) {
            fabricStorageView = (net.fabricmc.fabric.api.transfer.v1.storage.StorageView<T>) 
                ((ItemStorageImpl.FabricItemStorageViewWrapper) storageView).getFabricStorageView();
        }
        
        // Handle FluidStorageView wrappers
        if (storageView instanceof FluidStorageImpl.FabricFluidStorageViewWrapper) {
            fabricStorageView = (net.fabricmc.fabric.api.transfer.v1.storage.StorageView<T>) 
                ((FluidStorageImpl.FabricFluidStorageViewWrapper) storageView).getFabricStorageView();
        }
        
        // Convert our abstraction back to Fabric variant
        T fabricResource = resource;
        
        // Handle ItemVariant conversion
        if (resource instanceof ItemVariantImpl.FabricItemVariantWrapper) {
            fabricResource = (T) ((ItemVariantImpl.FabricItemVariantWrapper) resource).getFabricVariant();
        }
        // Handle FluidVariant conversion
        else if (resource instanceof FluidVariantImpl.FabricFluidVariantWrapper) {
            fabricResource = (T) ((FluidVariantImpl.FabricFluidVariantWrapper) resource).getFabricVariant();
        }
        
        return net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil.simulateExtract(
            fabricStorageView,
            fabricResource,
            maxAmount,
            (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
        );
    }
}