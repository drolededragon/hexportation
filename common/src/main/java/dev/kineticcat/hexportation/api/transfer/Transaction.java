package dev.kineticcat.hexportation.api.transfer;

import dev.architectury.injectables.annotations.ExpectPlatform;

/**
 * Cross-platform replacement for Fabric's Transaction.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
 * to dev.kineticcat.hexportation.api.transfer.Transaction
 */
public abstract class Transaction implements AutoCloseable {
    
    /**
     * Open an outer transaction.
     * This method signature matches Fabric's Transaction.openOuter()
     */
    @ExpectPlatform
    public static Transaction openOuter() {
        throw new AssertionError("Platform implementation required");
    }
    
    /**
     * Open a nested transaction within this transaction.
     * This method signature matches Fabric's Transaction.openNested()
     */
    public abstract Transaction openNested();
    
    /**
     * Commit this transaction.
     * This method signature matches Fabric's Transaction.commit()
     */
    public abstract void commit();
    
    /**
     * Abort this transaction.
     * This method signature matches Fabric's Transaction.abort()
     */
    public abstract void abort();
    
    @Override
    public abstract void close();
}