package dev.kineticcat.hexportation.forge.transfer;

import dev.kineticcat.hexportation.api.transfer.Transaction;

/**
 * Forge implementation that mocks Fabric's transaction system.
 * Since Forge doesn't have native transactions, this implements a compatible API
 * that provides the same interface but uses simulation-based approach.
 */
public class TransactionImpl extends Transaction {
    
    private boolean isCommitted = false;
    private boolean isAborted = false;
    private boolean isClosed = false;
    
    private TransactionImpl() {
        // Private constructor - use openOuter() to create instances
    }
    
    public static Transaction openOuter() {
        return new TransactionImpl();
    }
    
    @Override
    public Transaction openNested() {
        // Create a new independent transaction for Forge
        // Forge doesn't support true nesting, but we can create separate instances
        return new TransactionImpl();
    }
    
    @Override
    public void commit() {
        if (isClosed) {
            throw new IllegalStateException("Transaction is already closed");
        }
        if (isAborted) {
            throw new IllegalStateException("Cannot commit an aborted transaction");  
        }
        isCommitted = true;
    }
    
    @Override
    public void abort() {
        if (isClosed) {
            throw new IllegalStateException("Transaction is already closed");
        }
        isAborted = true;
    }
    
    @Override
    public void close() {
        if (isClosed) {
            return; // Already closed
        }
        
        // Auto-commit if not explicitly committed or aborted
        // This matches Fabric behavior where transactions auto-commit on close
        if (!isCommitted && !isAborted) {
            commit();
        }
        
        isClosed = true;
    }
    
    /**
     * Check if this transaction has been committed.
     */
    public boolean isCommitted() {
        return isCommitted;
    }
    
    /**
     * Check if this transaction has been aborted.
     */
    public boolean isAborted() {
        return isAborted;
    }
    
    /**
     * Check if this transaction has been closed.
     */
    public boolean isClosed() {
        return isClosed;
    }
    
    /**
     * Check if this transaction is valid (not aborted or closed).
     * This can be used by Storage implementations to decide whether to perform operations.
     */
    public boolean isValid() {
        return !isAborted && !isClosed;
    }
}