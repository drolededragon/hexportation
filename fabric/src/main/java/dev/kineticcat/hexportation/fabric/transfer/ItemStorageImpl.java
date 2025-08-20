package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.Storage;
import dev.kineticcat.hexportation.api.transfer.StorageView;
import dev.kineticcat.hexportation.api.transfer.ItemVariant;
import dev.kineticcat.hexportation.api.transfer.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import java.util.Iterator;

/**
 * Fabric implementation that delegates to actual Fabric ItemStorage.SIDED.find().
 */
public class ItemStorageImpl {
    
    /**
     * Singleton instance that implements our SidedStorage interface
     */
    public static final ItemStorage.SidedStorage INSTANCE = new ItemStorage.SidedStorage() {
        @Override
        public Storage<ItemVariant> find(ServerLevel level, BlockPos pos, Direction direction) {
            net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> fabricStorage = 
                net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.find(level, pos, direction);
            
            if (fabricStorage == null) {
                return null;
            }
            
            return new FabricItemStorageWrapper(fabricStorage);
        }
    };
    
    /**
     * Wrapper class that implements our Storage interface using Fabric's Storage.
     */
    public static class FabricItemStorageWrapper implements Storage<ItemVariant> {
        private final net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> fabricStorage;
        
        public FabricItemStorageWrapper(net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> fabricStorage) {
            this.fabricStorage = fabricStorage;
        }
        
        @Override
        public long insert(ItemVariant resource, long maxAmount, Object transaction) {
            ItemVariantImpl.FabricItemVariantWrapper wrapper = (ItemVariantImpl.FabricItemVariantWrapper) resource;
            return fabricStorage.insert(
                wrapper.getFabricVariant(),
                maxAmount,
                (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
            );
        }
        
        @Override
        public long simulateInsert(ItemVariant resource, long maxAmount, Object transaction) {
            ItemVariantImpl.FabricItemVariantWrapper wrapper = (ItemVariantImpl.FabricItemVariantWrapper) resource;
            return fabricStorage.simulateInsert(
                wrapper.getFabricVariant(),
                maxAmount,
                (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
            );
        }
        
        @Override
        public long extract(ItemVariant resource, long maxAmount, Object transaction) {
            ItemVariantImpl.FabricItemVariantWrapper wrapper = (ItemVariantImpl.FabricItemVariantWrapper) resource;
            return fabricStorage.extract(
                wrapper.getFabricVariant(),
                maxAmount,
                (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
            );
        }
        
        @Override
        public long simulateExtract(ItemVariant resource, long maxAmount, Object transaction) {
            ItemVariantImpl.FabricItemVariantWrapper wrapper = (ItemVariantImpl.FabricItemVariantWrapper) resource;
            return fabricStorage.simulateExtract(
                wrapper.getFabricVariant(),
                maxAmount,
                (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
            );
        }
        
        @Override
        public Iterator<StorageView<ItemVariant>> iterator() {
            return new FabricStorageViewIterator(fabricStorage.iterator());
        }
        
        @Override
        public Iterator<StorageView<ItemVariant>> nonEmptyIterator() {
            return new FabricStorageViewIterator(fabricStorage.nonEmptyIterator());
        }
        
        public net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> getFabricStorage() {
            return fabricStorage;
        }
    }
    
    /**
     * Iterator wrapper that converts Fabric StorageView to our StorageView.
     */
    private static class FabricStorageViewIterator implements Iterator<StorageView<ItemVariant>> {
        private final Iterator<? extends net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant>> fabricIterator;
        
        public FabricStorageViewIterator(Iterator<? extends net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant>> fabricIterator) {
            this.fabricIterator = fabricIterator;
        }
        
        @Override
        public boolean hasNext() {
            return fabricIterator.hasNext();
        }
        
        @Override
        public StorageView<ItemVariant> next() {
            return new FabricItemStorageViewWrapper(fabricIterator.next());
        }
    }
    
    /**
     * Wrapper class that implements our StorageView interface using Fabric's StorageView.
     */
    public static class FabricItemStorageViewWrapper implements StorageView<ItemVariant> {
        private final net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> fabricStorageView;
        
        public FabricItemStorageViewWrapper(net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> fabricStorageView) {
            this.fabricStorageView = fabricStorageView;
        }
        
        @Override
        public ItemVariant getResource() {
            return ItemVariantImpl.wrap(fabricStorageView.getResource());
        }
        
        @Override
        public long getAmount() {
            return fabricStorageView.getAmount();
        }
        
        @Override
        public long getCapacity() {
            return fabricStorageView.getCapacity();
        }
        
        @Override
        public boolean isBlank() {
            return fabricStorageView.getResource().isBlank();
        }
        
        @Override
        public long simulateExtract(ItemVariant resource, long maxAmount, Object transaction) {
            ItemVariantImpl.FabricItemVariantWrapper wrapper = (ItemVariantImpl.FabricItemVariantWrapper) resource;
            return net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil.simulateExtract(
                fabricStorageView,
                wrapper.getFabricVariant(),
                maxAmount,
                (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
            );
        }
        
        @Override
        public long extract(ItemVariant resource, long maxAmount, Object transaction) {
            ItemVariantImpl.FabricItemVariantWrapper wrapper = (ItemVariantImpl.FabricItemVariantWrapper) resource;
            return fabricStorageView.extract(
                wrapper.getFabricVariant(),
                maxAmount,
                (net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext) transaction
            );
        }
        
        public net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.item.ItemVariant> getFabricStorageView() {
            return fabricStorageView;
        }
    }
}