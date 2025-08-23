package dev.kineticcat.hexportation.forge.transfer

import dev.kineticcat.hexportation.api.transfer.EnergyStorageUtil

/**
 * Forge implementation of our cross-platform EnergyStorageUtil.
 * Since Forge doesn't have Team Reborn Energy's EnergyStorageUtil,
 * we implement the move operation manually using ForgeEnergyWrapper.
 */
class EnergyStorageUtilImpl : EnergyStorageUtil.Implementation() {
    
    companion object {
        @JvmField
        val INSTANCE = EnergyStorageUtilImpl()
    }
    
    /**
     * Implementation of EnergyStorageUtil.move() for Forge.
     * Simulates the behavior of Team Reborn Energy's EnergyStorageUtil.move()
     * using Forge's IEnergyStorage capabilities.
     */
    override fun move(source: Any?, sink: Any?, maxAmount: Long, transaction: Any?): Long {
        // Validate transaction state
        if (transaction is TransactionImpl.ForgeTransactionWrapper && !transaction.isValid()) {
            return 0 // Don't operate on aborted/closed transactions
        }
        
        // Cast to our ForgeEnergyWrapper instances
        val sourceStorage = source as EnergyStorageImpl.ForgeEnergyWrapper
        val sinkStorage = sink as EnergyStorageImpl.ForgeEnergyWrapper
        
        // Phase 1: Simulate extraction from source
        val simulatedExtraction = sourceStorage.simulateExtract(maxAmount)
        if (simulatedExtraction <= 0) return 0 // Can't extract anything
        
        // Phase 2: Simulate insertion into sink
        val simulatedInsertion = sinkStorage.simulateInsert(simulatedExtraction)
        if (simulatedInsertion <= 0) return 0 // Can't insert anything
        
        // Phase 3: Perform actual operations (extraction first, then insertion)
        val actualExtracted = sourceStorage.extract(simulatedInsertion, false)
        if (actualExtracted <= 0) return 0 // Extraction failed unexpectedly
        
        val actualInserted = sinkStorage.insert(actualExtracted, false)
        
        // Handle partial insertion (shouldn't happen with proper simulation, but be defensive)
        if (actualInserted < actualExtracted) {
            // The excess energy is effectively lost, but this should be extremely rare
            System.err.println("Warning: Partial insertion in EnergyStorageUtil.move() - " + 
                (actualExtracted - actualInserted) + " FE lost")
        }
        
        return actualInserted
    }
}