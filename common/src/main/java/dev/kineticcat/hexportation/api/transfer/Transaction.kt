package dev.kineticcat.hexportation.api.transfer

/**
 * Cross-platform replacement for Fabric's Transaction using singleton injection pattern.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
 * to dev.kineticcat.hexportation.api.transfer.Transaction
 */
abstract class Transaction : AutoCloseable {
    
    /**
     * Open a nested transaction within this transaction.
     * This method signature matches Fabric's Transaction.openNested()
     */
    abstract fun openNested(): Transaction
    
    /**
     * Commit this transaction.
     * This method signature matches Fabric's Transaction.commit()
     */
    abstract fun commit()
    
    /**
     * Abort this transaction.
     * This method signature matches Fabric's Transaction.abort()
     */
    abstract fun abort()
    
    abstract override fun close()
    
    /**
     * Platform-specific implementation interface.
     */
    abstract class Implementation {
        abstract fun openOuter(): Transaction
    }
    
    companion object {
        private var implementation: Implementation? = null
        
        /**
         * Open an outer transaction.
         * This method signature matches Fabric's Transaction.openOuter()
         */
        @JvmStatic
        fun openOuter(): Transaction {
            return implementation?.openOuter() ?: throw IllegalStateException("Transaction implementation not set")
        }
        
        /**
         * Set the platform-specific implementation.
         * Called by platform modules during initialization.
         */
        @JvmStatic
        fun setImplementation(impl: Implementation) {
            implementation = impl
        }
    }
}