package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.Storage;
import dev.kineticcat.hexportation.api.transfer.StorageView;
import dev.kineticcat.hexportation.api.transfer.FluidVariant;
import dev.kineticcat.hexportation.api.transfer.FluidStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import java.util.Iterator;

/**
 * Fabric implementation that delegates to actual Fabric FluidStorage.SIDED.find().
 */
public class FluidStorageImpl {
    
    /**
     * Singleton instance that implements our SidedStorage interface
     */
    public static final FluidStorage.SidedStorage INSTANCE = new FluidStorage.SidedStorage() {
        @Override
        public Storage<FluidVariant> find(ServerLevel level, BlockPos pos, Direction direction) {
            net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant> fabricStorage = 
                net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage.SIDED.find(level, pos, direction);
            
            if (fabricStorage == null) {
                return null;
            }
            
            return new FabricFluidStorageWrapper(fabricStorage);
        }
    };
    
    /**
     * Wrapper class that implements our Storage interface using Fabric's Storage.
     */
    public static class FabricFluidStorageWrapper implements Storage<FluidVariant> {
        private final net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant> fabricStorage;
        
        public FabricFluidStorageWrapper(net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant> fabricStorage) {
            this.fabricStorage = fabricStorage;
        }
        
        @Override
        public long insert(FluidVariant resource, long maxAmount, Object transaction) {
            FluidVariantImpl.FabricFluidVariantWrapper wrapper = (FluidVariantImpl.FabricFluidVariantWrapper) resource;
            return fabricStorage.insert(
                wrapper.getFabricVariant(),
                maxAmount,
                (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
            );
        }
        
        @Override
        public long simulateInsert(FluidVariant resource, long maxAmount, Object transaction) {
            FluidVariantImpl.FabricFluidVariantWrapper wrapper = (FluidVariantImpl.FabricFluidVariantWrapper) resource;
            return fabricStorage.simulateInsert(
                wrapper.getFabricVariant(),
                maxAmount,
                (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
            );
        }
        
        @Override
        public long extract(FluidVariant resource, long maxAmount, Object transaction) {
            FluidVariantImpl.FabricFluidVariantWrapper wrapper = (FluidVariantImpl.FabricFluidVariantWrapper) resource;
            return fabricStorage.extract(
                wrapper.getFabricVariant(),
                maxAmount,
                (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
            );
        }
        
        @Override
        public long simulateExtract(FluidVariant resource, long maxAmount, Object transaction) {
            FluidVariantImpl.FabricFluidVariantWrapper wrapper = (FluidVariantImpl.FabricFluidVariantWrapper) resource;
            return fabricStorage.simulateExtract(
                wrapper.getFabricVariant(),
                maxAmount,
                (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
            );
        }
        
        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            return new FabricStorageViewIterator(fabricStorage.iterator());
        }
        
        @Override
        public Iterator<StorageView<FluidVariant>> nonEmptyIterator() {
            return new FabricStorageViewIterator(fabricStorage.nonEmptyIterator());
        }
        
        public net.fabricmc.fabric.api.transfer.v1.storage.Storage<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant> getFabricStorage() {
            return fabricStorage;
        }
    }
    
    /**
     * Iterator wrapper that converts Fabric StorageView to our StorageView.
     */
    private static class FabricStorageViewIterator implements Iterator<StorageView<FluidVariant>> {
        private final Iterator<? extends net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant>> fabricIterator;
        
        public FabricStorageViewIterator(Iterator<? extends net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant>> fabricIterator) {
            this.fabricIterator = fabricIterator;
        }
        
        @Override
        public boolean hasNext() {
            return fabricIterator.hasNext();
        }
        
        @Override
        public StorageView<FluidVariant> next() {
            return new FabricFluidStorageViewWrapper(fabricIterator.next());
        }
    }
    
    /**
     * Wrapper class that implements our StorageView interface using Fabric's StorageView.
     */
    public static class FabricFluidStorageViewWrapper implements StorageView<FluidVariant> {
        private final net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant> fabricStorageView;
        
        public FabricFluidStorageViewWrapper(net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant> fabricStorageView) {
            this.fabricStorageView = fabricStorageView;
        }
        
        @Override
        public FluidVariant getResource() {
            return FluidVariantImpl.wrap(fabricStorageView.getResource());
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
        public long simulateExtract(FluidVariant resource, long maxAmount, Object transaction) {
            FluidVariantImpl.FabricFluidVariantWrapper wrapper = (FluidVariantImpl.FabricFluidVariantWrapper) resource;
            return net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil.simulateExtract(
                fabricStorageView,
                wrapper.getFabricVariant(),
                maxAmount,
                (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
            );
        }
        
        @Override
        public long extract(FluidVariant resource, long maxAmount, Object transaction) {
            FluidVariantImpl.FabricFluidVariantWrapper wrapper = (FluidVariantImpl.FabricFluidVariantWrapper) resource;
            return fabricStorageView.extract(
                wrapper.getFabricVariant(),
                maxAmount,
                (net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext) transaction
            );
        }
        
        public net.fabricmc.fabric.api.transfer.v1.storage.StorageView<net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant> getFabricStorageView() {
            return fabricStorageView;
        }
    }
}