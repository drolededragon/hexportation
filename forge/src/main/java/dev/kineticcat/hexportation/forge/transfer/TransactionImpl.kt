package dev.kineticcat.hexportation.forge.transfer

import dev.kineticcat.hexportation.api.transfer.Transaction

/**
 * Forge implementation using singleton injection pattern.
 */
class TransactionImpl : Transaction.Implementation() {
    
    companion object {
        @JvmField
        val INSTANCE = TransactionImpl()
    }
    
    override fun openOuter(): Transaction = ForgeTransactionWrapper()
    
    /**
     * Wrapper class that extends our Transaction abstract class with Forge mock implementation.
     */
    class ForgeTransactionWrapper : Transaction() {
        
        private var isCommitted = false
        private var isAborted = false
        private var isClosed = false
        
        override fun openNested(): Transaction {
            // Create a new independent transaction for Forge
            // Forge doesn't support true nesting, but we can create separate instances
            return ForgeTransactionWrapper()
        }
        
        override fun commit() {
            check(!isClosed) { "Transaction is already closed" }
            check(!isAborted) { "Cannot commit an aborted transaction" }
            isCommitted = true
        }
        
        override fun abort() {
            check(!isClosed) { "Transaction is already closed" }
            isAborted = true
        }
        
        override fun close() {
            if (isClosed) return // Already closed
            
            // Auto-commit if not explicitly committed or aborted
            // This matches Fabric behavior where transactions auto-commit on close
            if (!isCommitted && !isAborted) {
                commit()
            }
            
            isClosed = true
        }
        
        /**
         * Check if this transaction has been committed.
         */
        fun isCommitted() = isCommitted
        
        /**
         * Check if this transaction has been aborted.
         */
        fun isAborted() = isAborted
        
        /**
         * Check if this transaction has been closed.
         */
        fun isClosed() = isClosed
        
        /**
         * Check if this transaction is valid (not aborted or closed).
         * This can be used by Storage implementations to decide whether to perform operations.
         */
        fun isValid() = !isAborted && !isClosed
    }
}