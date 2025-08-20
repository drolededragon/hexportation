package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.Transaction;

/**
 * Fabric implementation using singleton injection pattern.
 */
public class TransactionImpl extends Transaction.Implementation {
    
    public static final TransactionImpl INSTANCE = new TransactionImpl();
    
    @Override
    public Transaction openOuter() {
        return new FabricTransactionWrapper(net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter());
    }
    
    /**
     * Wrapper class that extends our Transaction abstract class using Fabric's Transaction.
     */
    public static class FabricTransactionWrapper extends Transaction {
        
        private final net.fabricmc.fabric.api.transfer.v1.transaction.Transaction fabricTransaction;
        
        public FabricTransactionWrapper(net.fabricmc.fabric.api.transfer.v1.transaction.Transaction fabricTransaction) {
            this.fabricTransaction = fabricTransaction;
        }
        
        @Override
        public Transaction openNested() {
            return new FabricTransactionWrapper(fabricTransaction.openNested());
        }
        
        @Override
        public void commit() {
            fabricTransaction.commit();
        }
        
        @Override
        public void abort() {
            fabricTransaction.abort();
        }
        
        @Override
        public void close() {
            fabricTransaction.close();
        }
        
        /**
         * Get the underlying Fabric transaction for platform-specific operations.
         */
        public net.fabricmc.fabric.api.transfer.v1.transaction.Transaction getFabricTransaction() {
            return fabricTransaction;
        }
    }
}