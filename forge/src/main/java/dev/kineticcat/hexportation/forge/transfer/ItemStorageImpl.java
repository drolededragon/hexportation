package dev.kineticcat.hexportation.forge.transfer;

import dev.kineticcat.hexportation.api.transfer.Storage;
import dev.kineticcat.hexportation.api.transfer.StorageView;
import dev.kineticcat.hexportation.api.transfer.ItemVariant;
import dev.kineticcat.hexportation.api.transfer.ItemStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Forge implementation that delegates to Forge's IItemHandler capability system.
 * Provides identical API to Fabric ItemStorage through wrapper pattern.
 */
public class ItemStorageImpl {
    
    /**
     * Singleton instance that implements our SidedStorage interface
     */
    public static final ItemStorage.SidedStorage INSTANCE = new ItemStorage.SidedStorage() {
        @Override
        public Storage<ItemVariant> find(ServerLevel level, BlockPos pos, Direction direction) {
            var blockEntity = level.getBlockEntity(pos);
            System.out.println("[ItemStorage] Checking pos=" + pos + " direction=" + direction + " blockEntity=" + (blockEntity != null ? blockEntity.getClass().getSimpleName() : "null"));
            
            if (blockEntity == null) {
                System.out.println("[ItemStorage] No blockEntity found, returning null");
                return null;
            }
            
            LazyOptional<IItemHandler> capability = blockEntity.getCapability(
                ForgeCapabilities.ITEM_HANDLER, direction);
            
            boolean hasCapability = capability.isPresent();
            System.out.println("[ItemStorage] Item capability present: " + hasCapability);
            
            if (hasCapability) {
                IItemHandler handler = capability.orElse(null);
                System.out.println("[ItemStorage] Found IItemHandler: " + handler.getClass().getSimpleName() + " with " + handler.getSlots() + " slots");
                return capability.map(ForgeItemWrapper::new).orElse(null);
            } else {
                System.out.println("[ItemStorage] No item capability, returning null");
                return null;
            }
        }
    };
    
    /**
     * Wrapper class that implements our Storage<ItemVariant> interface using Forge's IItemHandler.
     * Follows the same pattern as ForgeEnergyWrapper.
     */
    public static class ForgeItemWrapper implements Storage<ItemVariant> {
        private final IItemHandler forgeHandler;
        
        public ForgeItemWrapper(IItemHandler forgeHandler) {
            this.forgeHandler = forgeHandler;
        }
        
        @Override
        public long insert(ItemVariant resource, long maxAmount, Object transaction) {
            // Convert ItemVariant back to ItemStack for Forge
            ItemStack stack = resource.toStack((int) Math.min(maxAmount, Integer.MAX_VALUE));
            
            // Try to insert into any available slot
            for (int slot = 0; slot < forgeHandler.getSlots(); slot++) {
                if (forgeHandler.isItemValid(slot, stack)) {
                    ItemStack remaining = forgeHandler.insertItem(slot, stack, false);
                    long inserted = stack.getCount() - remaining.getCount();
                    if (inserted > 0) {
                        return inserted;
                    }
                }
            }
            return 0;
        }
        
        @Override
        public long simulateInsert(ItemVariant resource, long maxAmount, Object transaction) {
            // Convert ItemVariant back to ItemStack for Forge
            ItemStack stack = resource.toStack((int) Math.min(maxAmount, Integer.MAX_VALUE));
            
            // Simulate insertion into any available slot
            for (int slot = 0; slot < forgeHandler.getSlots(); slot++) {
                if (forgeHandler.isItemValid(slot, stack)) {
                    ItemStack remaining = forgeHandler.insertItem(slot, stack, true); // simulate=true
                    long inserted = stack.getCount() - remaining.getCount();
                    if (inserted > 0) {
                        return inserted;
                    }
                }
            }
            return 0;
        }
        
        @Override
        public long extract(ItemVariant resource, long maxAmount, Object transaction) {
            // Try to extract from any slot containing this resource
            for (int slot = 0; slot < forgeHandler.getSlots(); slot++) {
                ItemStack slotStack = forgeHandler.getStackInSlot(slot);
                if (!slotStack.isEmpty() && ItemVariant.of(slotStack.getItem()).equals(resource)) {
                    ItemStack extracted = forgeHandler.extractItem(slot, (int) Math.min(maxAmount, Integer.MAX_VALUE), false);
                    return extracted.getCount();
                }
            }
            return 0;
        }
        
        @Override
        public long simulateExtract(ItemVariant resource, long maxAmount, Object transaction) {
            // Try to simulate extraction from any slot containing this resource
            for (int slot = 0; slot < forgeHandler.getSlots(); slot++) {
                ItemStack slotStack = forgeHandler.getStackInSlot(slot);
                if (!slotStack.isEmpty() && ItemVariant.of(slotStack.getItem()).equals(resource)) {
                    ItemStack extracted = forgeHandler.extractItem(slot, (int) Math.min(maxAmount, Integer.MAX_VALUE), true); // simulate=true
                    return extracted.getCount();
                }
            }
            return 0;
        }
        
        @Override
        public Iterator<StorageView<ItemVariant>> iterator() {
            List<StorageView<ItemVariant>> views = new ArrayList<>();
            for (int i = 0; i < forgeHandler.getSlots(); i++) {
                views.add(new ForgeItemSlotView(forgeHandler, i));
            }
            return views.iterator();
        }
        
        @Override
        public Iterator<StorageView<ItemVariant>> nonEmptyIterator() {
            List<StorageView<ItemVariant>> views = new ArrayList<>();
            for (int i = 0; i < forgeHandler.getSlots(); i++) {
                ItemStack stack = forgeHandler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    views.add(new ForgeItemSlotView(forgeHandler, i));
                }
            }
            return views.iterator();
        }
        
        public IItemHandler getForgeHandler() {
            return forgeHandler;
        }
    }
    
    /**
     * Wrapper for individual slots that implements StorageView<ItemVariant>.
     * Represents one slot of the IItemHandler as a Fabric-style StorageView.
     */
    public static class ForgeItemSlotView implements StorageView<ItemVariant> {
        private final IItemHandler handler;
        private final int slot;
        
        public ForgeItemSlotView(IItemHandler handler, int slot) {
            this.handler = handler;
            this.slot = slot;
        }
        
        @Override
        public ItemVariant getResource() {
            ItemStack stack = handler.getStackInSlot(slot);
            if (stack.isEmpty()) {
                return ItemVariant.blank(); // Empty variant for empty slots
            }
            return ItemVariant.of(stack.getItem());
        }
        
        @Override
        public long getAmount() {
            return handler.getStackInSlot(slot).getCount();
        }
        
        @Override
        public long getCapacity() {
            // Forge doesn't have a direct capacity concept per slot
            // Use slot limit as approximation
            return handler.getSlotLimit(slot);
        }
        
        @Override
        public boolean isBlank() {
            return handler.getStackInSlot(slot).isEmpty();
        }
        
        @Override
        public long simulateExtract(ItemVariant resource, long maxAmount, Object transaction) {
            ItemStack slotStack = handler.getStackInSlot(slot);
            if (slotStack.isEmpty() || !ItemVariant.of(slotStack.getItem()).equals(resource)) {
                return 0;
            }
            
            ItemStack extracted = handler.extractItem(slot, (int) Math.min(maxAmount, Integer.MAX_VALUE), true); // simulate=true
            return extracted.getCount();
        }
        
        @Override
        public long extract(ItemVariant resource, long maxAmount, Object transaction) {
            ItemStack slotStack = handler.getStackInSlot(slot);
            if (slotStack.isEmpty() || !ItemVariant.of(slotStack.getItem()).equals(resource)) {
                return 0;
            }
            
            ItemStack extracted = handler.extractItem(slot, (int) Math.min(maxAmount, Integer.MAX_VALUE), false);
            return extracted.getCount();
        }
    }
}