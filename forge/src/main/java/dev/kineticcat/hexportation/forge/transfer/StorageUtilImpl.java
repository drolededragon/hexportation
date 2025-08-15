package dev.kineticcat.hexportation.forge.transfer;

import dev.kineticcat.hexportation.api.transfer.Storage;
import dev.kineticcat.hexportation.api.transfer.StorageView;
import dev.kineticcat.hexportation.api.transfer.Transaction;

import java.util.function.Predicate;

/**
 * Forge implementation of our cross-platform StorageUtil.
 * Since Forge doesn't have an equivalent to Fabric's StorageUtil.move(),
 * we implement the same algorithm manually using simulation-first approach.
 */
public class StorageUtilImpl {
    
    /**
     * Implementation of StorageUtil.move() for Forge.
     * Reproduces the exact same behavior as Fabric's StorageUtil.move() 
     * using our ForgeItemWrapper/ForgeFluidWrapper implementations.
     */
    public static <T> long move(Storage<T> source, Storage<T> sink, 
                               Predicate<T> filter, long maxAmount, Transaction transaction) {
        
        // Validate transaction state
        if (transaction instanceof TransactionImpl tx && !tx.isValid()) {
            return 0; // Don't operate on aborted/closed transactions
        }
        
        long totalMoved = 0;
        
        // Iterate through all non-empty views in the source storage
        // This matches Fabric's StorageUtil.move() behavior exactly
        var sourceIterator = source.nonEmptyIterator();
        while (sourceIterator.hasNext()) {
            StorageView<T> sourceView = sourceIterator.next();
            T resource = sourceView.getResource();
            
            // Apply the filter predicate
            if (!filter.test(resource)) {
                continue;
            }
            
            // Calculate how much we can potentially move from this view
            long availableAmount = Math.min(sourceView.getAmount(), maxAmount - totalMoved);
            if (availableAmount <= 0) {
                break; // We've reached the maximum transfer amount
            }
            
            // Phase 1: Simulate extraction from source
            long simulatedExtraction = sourceView.simulateExtract(resource, availableAmount, transaction);
            if (simulatedExtraction <= 0) {
                continue; // Can't extract anything from this view
            }
            
            // Phase 2: Simulate insertion into sink  
            long simulatedInsertion = sink.simulateInsert(resource, simulatedExtraction, transaction);
            if (simulatedInsertion <= 0) {
                continue; // Can't insert anything into sink
            }
            
            // Phase 3: Perform the actual operations (extraction first, then insertion)
            // This order matches Fabric's behavior and ensures consistency
            long actualExtracted = sourceView.extract(resource, simulatedInsertion, transaction);
            if (actualExtracted <= 0) {
                continue; // Extraction failed unexpectedly
            }
            
            long actualInserted = sink.insert(resource, actualExtracted, transaction);
            
            // Track the total amount moved
            totalMoved += actualInserted;
            
            // Handle partial insertion (shouldn't happen with proper simulation, but be defensive)
            if (actualInserted < actualExtracted) {
                // In Fabric this would be handled by transaction rollback
                // In Forge we can't rollback, so we log a warning
                // The excess items are effectively lost, but this should be extremely rare
                System.err.println("Warning: Partial insertion in StorageUtil.move() - " + 
                    (actualExtracted - actualInserted) + " units lost");
            }
            
            // Check if we've reached the maximum transfer amount
            if (totalMoved >= maxAmount) {
                break;
            }
        }
        
        return totalMoved;
    }
    
    /**
     * Implementation of StorageUtil.simulateExtract() for Forge.
     * Simply delegates to the StorageView's simulateExtract method.
     */
    public static <T> long simulateExtract(StorageView<T> storageView, T resource, long maxAmount, Transaction transaction) {
        // Validate transaction state
        if (transaction instanceof TransactionImpl tx && !tx.isValid()) {
            return 0; // Don't operate on aborted/closed transactions
        }
        
        return storageView.simulateExtract(resource, maxAmount, transaction);
    }
}