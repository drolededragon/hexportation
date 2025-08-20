package dev.kineticcat.hexportation.forge.transfer;

import dev.kineticcat.hexportation.api.transfer.Transaction;

/**
 * Forge implementation using singleton injection pattern.
 */
public class TransactionImpl extends Transaction.Implementation {
    
    public static final TransactionImpl INSTANCE = new TransactionImpl();
    
    @Override
    public Transaction openOuter() {
        return new ForgeTransactionWrapper();
    }
    
    /**
     * Wrapper class that extends our Transaction abstract class with Forge mock implementation.
     */
    public static class ForgeTransactionWrapper extends Transaction {
        
        private boolean isCommitted = false;
        private boolean isAborted = false;
        private boolean isClosed = false;
        
        public ForgeTransactionWrapper() {
            // Constructor for creating transaction instances
        }
        
        @Override
        public Transaction openNested() {
            // Create a new independent transaction for Forge
            // Forge doesn't support true nesting, but we can create separate instances
            return new ForgeTransactionWrapper();
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
}