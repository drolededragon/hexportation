package dev.kineticcat.hexportation.api.transfer;

/**
 * Cross-platform replacement for Fabric's Transaction using singleton injection pattern.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
 * to dev.kineticcat.hexportation.api.transfer.Transaction
 */
public abstract class Transaction implements AutoCloseable {
    
    private static Implementation implementation;
    
    /**
     * Open an outer transaction.
     * This method signature matches Fabric's Transaction.openOuter()
     */
    public static Transaction openOuter() {
        return implementation.openOuter();
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
    
    /**
     * Platform-specific implementation interface.
     */
    public static abstract class Implementation {
        public abstract Transaction openOuter();
    }
    
    /**
     * Set the platform-specific implementation.
     * Called by platform modules during initialization.
     */
    public static void setImplementation(Implementation impl) {
        implementation = impl;
    }
}