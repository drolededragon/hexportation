package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.Transaction;

/**
 * Fabric implementation that wraps actual Fabric Transaction.
 * Delegates all operations to the underlying Fabric transaction.
 */
public class TransactionImpl extends Transaction {
    
    private final net.fabricmc.fabric.api.transfer.v1.transaction.Transaction fabricTransaction;
    
    private TransactionImpl(net.fabricmc.fabric.api.transfer.v1.transaction.Transaction fabricTransaction) {
        this.fabricTransaction = fabricTransaction;
    }
    
    public static Transaction openOuter() {
        return new TransactionImpl(net.fabricmc.fabric.api.transfer.v1.transaction.Transaction.openOuter());
    }
    
    @Override
    public Transaction openNested() {
        return new TransactionImpl(fabricTransaction.openNested());
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