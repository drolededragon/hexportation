package dev.kineticcat.hexportation.forge.transfer;

import dev.kineticcat.hexportation.api.transfer.Storage;
import dev.kineticcat.hexportation.api.transfer.StorageView;
import dev.kineticcat.hexportation.api.transfer.FluidVariant;
import dev.kineticcat.hexportation.api.transfer.FluidStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Forge implementation that delegates to Forge's IFluidHandler capability system.
 * Provides identical API to Fabric FluidStorage through wrapper pattern.
 */
public class FluidStorageImpl {
    
    /**
     * Singleton instance that implements our SidedStorage interface
     */
    public static final FluidStorage.SidedStorage INSTANCE = new FluidStorage.SidedStorage() {
        @Override
        public Storage<FluidVariant> find(ServerLevel level, BlockPos pos, Direction direction) {
            var blockEntity = level.getBlockEntity(pos);
            System.out.println("[FluidStorage] Checking pos=" + pos + " direction=" + direction + " blockEntity=" + (blockEntity != null ? blockEntity.getClass().getSimpleName() : "null"));
            
            if (blockEntity == null) {
                System.out.println("[FluidStorage] No blockEntity found, returning null");
                return null;
            }
            
            LazyOptional<IFluidHandler> capability = blockEntity.getCapability(
                ForgeCapabilities.FLUID_HANDLER, direction);
            
            boolean hasCapability = capability.isPresent();
            System.out.println("[FluidStorage] Fluid capability present: " + hasCapability);
            
            if (hasCapability) {
                IFluidHandler handler = capability.orElse(null);
                System.out.println("[FluidStorage] Found IFluidHandler: " + handler.getClass().getSimpleName() + " with " + handler.getTanks() + " tanks");
                for (int i = 0; i < handler.getTanks(); i++) {
                    var stack = handler.getFluidInTank(i);
                    System.out.println("[FluidStorage] Tank " + i + ": " + stack.getAmount() + "mB of " + (stack.isEmpty() ? "air" : stack.getFluid().toString()));
                }
                return capability.map(ForgeFluidWrapper::new).orElse(null);
            } else {
                System.out.println("[FluidStorage] No fluid capability, returning null");
                return null;
            }
        }
    };
    
    /**
     * Wrapper class that implements our Storage<FluidVariant> interface using Forge's IFluidHandler.
     * Follows the same pattern as ForgeItemWrapper.
     */
    public static class ForgeFluidWrapper implements Storage<FluidVariant> {
        private final IFluidHandler forgeHandler;
        
        public ForgeFluidWrapper(IFluidHandler forgeHandler) {
            this.forgeHandler = forgeHandler;
        }
        
        @Override
        public long insert(FluidVariant resource, long maxAmount, Object transaction) {
            // Convert FluidVariant back to FluidStack for Forge
            FluidStack stack = new FluidStack(resource.getFluid(), (int) Math.min(maxAmount, Integer.MAX_VALUE));
            if (resource.getNbt() != null) {
                stack.setTag(resource.getNbt());
            }
            
            // Try to fill any available tank
            int filled = forgeHandler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
            return filled;
        }
        
        @Override
        public long simulateInsert(FluidVariant resource, long maxAmount, Object transaction) {
            // Convert FluidVariant back to FluidStack for Forge
            FluidStack stack = new FluidStack(resource.getFluid(), (int) Math.min(maxAmount, Integer.MAX_VALUE));
            if (resource.getNbt() != null) {
                stack.setTag(resource.getNbt());
            }
            
            // Simulate filling any available tank
            int filled = forgeHandler.fill(stack, IFluidHandler.FluidAction.SIMULATE);
            return filled;
        }
        
        @Override
        public long extract(FluidVariant resource, long maxAmount, Object transaction) {
            // Try to drain from any tank containing this resource
            FluidStack toDrain = new FluidStack(resource.getFluid(), (int) Math.min(maxAmount, Integer.MAX_VALUE));
            if (resource.getNbt() != null) {
                toDrain.setTag(resource.getNbt());
            }
            
            FluidStack drained = forgeHandler.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
            return drained.isEmpty() ? 0 : drained.getAmount();
        }
        
        @Override
        public long simulateExtract(FluidVariant resource, long maxAmount, Object transaction) {
            // Try to simulate draining from any tank containing this resource
            FluidStack toDrain = new FluidStack(resource.getFluid(), (int) Math.min(maxAmount, Integer.MAX_VALUE));
            if (resource.getNbt() != null) {
                toDrain.setTag(resource.getNbt());
            }
            
            FluidStack drained = forgeHandler.drain(toDrain, IFluidHandler.FluidAction.SIMULATE);
            return drained.isEmpty() ? 0 : drained.getAmount();
        }
        
        @Override
        public Iterator<StorageView<FluidVariant>> iterator() {
            List<StorageView<FluidVariant>> views = new ArrayList<>();
            for (int i = 0; i < forgeHandler.getTanks(); i++) {
                views.add(new ForgeFluidTankView(forgeHandler, i));
            }
            return views.iterator();
        }
        
        @Override
        public Iterator<StorageView<FluidVariant>> nonEmptyIterator() {
            List<StorageView<FluidVariant>> views = new ArrayList<>();
            for (int i = 0; i < forgeHandler.getTanks(); i++) {
                FluidStack stack = forgeHandler.getFluidInTank(i);
                if (!stack.isEmpty()) {
                    views.add(new ForgeFluidTankView(forgeHandler, i));
                }
            }
            return views.iterator();
        }
        
        public IFluidHandler getForgeHandler() {
            return forgeHandler;
        }
    }
    
    /**
     * Wrapper for individual tanks that implements StorageView<FluidVariant>.
     * Represents one tank of the IFluidHandler as a Fabric-style StorageView.
     */
    public static class ForgeFluidTankView implements StorageView<FluidVariant> {
        private final IFluidHandler handler;
        private final int tank;
        
        public ForgeFluidTankView(IFluidHandler handler, int tank) {
            this.handler = handler;
            this.tank = tank;
        }
        
        @Override
        public FluidVariant getResource() {
            FluidStack stack = handler.getFluidInTank(tank);
            return FluidVariant.of(stack.getFluid(), stack.getTag());
        }
        
        @Override
        public long getAmount() {
            return handler.getFluidInTank(tank).getAmount();
        }
        
        @Override
        public long getCapacity() {
            return handler.getTankCapacity(tank);
        }
        
        @Override
        public boolean isBlank() {
            return handler.getFluidInTank(tank).isEmpty();
        }
        
        @Override
        public long simulateExtract(FluidVariant resource, long maxAmount, Object transaction) {
            FluidStack tankStack = handler.getFluidInTank(tank);
            if (tankStack.isEmpty() || !tankStack.getFluid().equals(resource.getFluid())) {
                return 0;
            }
            
            // For specific tank operations, we need to create a FluidStack to drain
            FluidStack toDrain = new FluidStack(resource.getFluid(), (int) Math.min(maxAmount, Integer.MAX_VALUE));
            if (resource.getNbt() != null) {
                toDrain.setTag(resource.getNbt());
            }
            
            FluidStack drained = handler.drain(toDrain, IFluidHandler.FluidAction.SIMULATE);
            return drained.isEmpty() ? 0 : drained.getAmount();
        }
        
        @Override
        public long extract(FluidVariant resource, long maxAmount, Object transaction) {
            FluidStack tankStack = handler.getFluidInTank(tank);
            if (tankStack.isEmpty() || !tankStack.getFluid().equals(resource.getFluid())) {
                return 0;
            }
            
            // For specific tank operations, we need to create a FluidStack to drain
            FluidStack toDrain = new FluidStack(resource.getFluid(), (int) Math.min(maxAmount, Integer.MAX_VALUE));
            if (resource.getNbt() != null) {
                toDrain.setTag(resource.getNbt());
            }
            
            FluidStack drained = handler.drain(toDrain, IFluidHandler.FluidAction.EXECUTE);
            return drained.isEmpty() ? 0 : drained.getAmount();
        }
    }
}