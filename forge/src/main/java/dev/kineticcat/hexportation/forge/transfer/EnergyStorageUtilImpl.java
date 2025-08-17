package dev.kineticcat.hexportation.forge.transfer;

import dev.kineticcat.hexportation.api.transfer.EnergyStorage;
import dev.kineticcat.hexportation.api.transfer.Transaction;

/**
 * Forge implementation of our cross-platform EnergyStorageUtil.
 * Since Forge doesn't have Team Reborn Energy's EnergyStorageUtil,
 * we implement the move operation manually using ForgeEnergyWrapper.
 */
public class EnergyStorageUtilImpl {
    
    /**
     * Implementation of EnergyStorageUtil.move() for Forge.
     * Simulates the behavior of Team Reborn Energy's EnergyStorageUtil.move()
     * using Forge's IEnergyStorage capabilities.
     */
    public static long move(Object source, Object sink, long maxAmount, Object transaction) {
        // Validate transaction state
        if (transaction instanceof TransactionImpl tx && !tx.isValid()) {
            return 0; // Don't operate on aborted/closed transactions
        }
        
        // Cast to our ForgeEnergyWrapper instances
        EnergyStorageImpl.ForgeEnergyWrapper sourceStorage = (EnergyStorageImpl.ForgeEnergyWrapper) source;
        EnergyStorageImpl.ForgeEnergyWrapper sinkStorage = (EnergyStorageImpl.ForgeEnergyWrapper) sink;
        
        // Phase 1: Simulate extraction from source
        long simulatedExtraction = sourceStorage.simulateExtract(maxAmount);
        if (simulatedExtraction <= 0) {
            return 0; // Can't extract anything
        }
        
        // Phase 2: Simulate insertion into sink
        long simulatedInsertion = sinkStorage.simulateInsert(simulatedExtraction);
        if (simulatedInsertion <= 0) {
            return 0; // Can't insert anything
        }
        
        // Phase 3: Perform actual operations (extraction first, then insertion)
        long actualExtracted = sourceStorage.extract(simulatedInsertion, false);
        if (actualExtracted <= 0) {
            return 0; // Extraction failed unexpectedly
        }
        
        long actualInserted = sinkStorage.insert(actualExtracted, false);
        
        // Handle partial insertion (shouldn't happen with proper simulation, but be defensive)
        if (actualInserted < actualExtracted) {
            // The excess energy is effectively lost, but this should be extremely rare
            System.err.println("Warning: Partial insertion in EnergyStorageUtil.move() - " + 
                (actualExtracted - actualInserted) + " FE lost");
        }
        
        return actualInserted;
    }
}