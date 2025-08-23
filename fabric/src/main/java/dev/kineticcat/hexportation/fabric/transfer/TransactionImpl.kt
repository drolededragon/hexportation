package dev.kineticcat.hexportation.fabric.transfer

import dev.kineticcat.hexportation.api.transfer.Transaction
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction as FabricTransaction

/**
 * Fabric implementation using singleton injection pattern.
 */
class TransactionImpl : Transaction.Implementation() {
    
    companion object {
        @JvmField
        val INSTANCE = TransactionImpl()
    }
    
    override fun openOuter(): Transaction = 
        FabricTransactionWrapper(FabricTransaction.openOuter())
    
    /**
     * Wrapper class that extends our Transaction abstract class using Fabric's Transaction.
     */
    class FabricTransactionWrapper(
        private val fabricTransaction: FabricTransaction
    ) : Transaction() {
        
        override fun openNested(): Transaction = 
            FabricTransactionWrapper(fabricTransaction.openNested())
        
        override fun commit() = fabricTransaction.commit()
        
        override fun abort() = fabricTransaction.abort()
        
        override fun close() = fabricTransaction.close()
        
        /**
         * Get the underlying Fabric transaction for platform-specific operations.
         */
        fun getFabricTransaction() = fabricTransaction
    }
}