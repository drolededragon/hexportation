package dev.kineticcat.hexportation.forge.transfer;

import dev.kineticcat.hexportation.api.transfer.EnergyStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

/**
 * Forge implementation that delegates to Forge's IEnergyStorage capability system.
 * Provides identical API to Team Reborn Energy through wrapper pattern.
 */
public class EnergyStorageImpl {
    
    public static EnergyStorage find(ServerLevel level, BlockPos pos, Direction direction) {
        var blockEntity = level.getBlockEntity(pos);
        if (blockEntity == null) return null;
        
        LazyOptional<IEnergyStorage> capability = blockEntity.getCapability(
            ForgeCapabilities.ENERGY, direction);
        
        return capability.map(ForgeEnergyWrapper::new).orElse(null);
    }
    
    public static long getAmount(Object energyStorage) {
        if (energyStorage instanceof ForgeEnergyWrapper wrapper) {
            return wrapper.getAmount();
        }
        if (energyStorage instanceof IEnergyStorage storage) {
            return storage.getEnergyStored();
        }
        return 0;
    }
    
    public static long getCapacity(Object energyStorage) {
        if (energyStorage instanceof ForgeEnergyWrapper wrapper) {
            return wrapper.getCapacity();
        }
        if (energyStorage instanceof IEnergyStorage storage) {
            return storage.getMaxEnergyStored();
        }
        return 0;
    }
    
    /**
     * Wrapper class that implements our EnergyStorage interface using Forge's IEnergyStorage.
     */
    public static class ForgeEnergyWrapper implements EnergyStorage {
        private final IEnergyStorage forgeStorage;
        
        public ForgeEnergyWrapper(IEnergyStorage forgeStorage) {
            this.forgeStorage = forgeStorage;
        }
        
        @Override
        public long getAmount() {
            return forgeStorage.getEnergyStored();
        }
        
        @Override
        public long getCapacity() {
            return forgeStorage.getMaxEnergyStored();
        }
        
        public long simulateExtract(long maxExtract) {
            return forgeStorage.extractEnergy((int) Math.min(maxExtract, Integer.MAX_VALUE), true);
        }
        
        public long simulateInsert(long maxInsert) {
            return forgeStorage.receiveEnergy((int) Math.min(maxInsert, Integer.MAX_VALUE), true);
        }
        
        public long extract(long maxExtract, boolean simulate) {
            return forgeStorage.extractEnergy((int) Math.min(maxExtract, Integer.MAX_VALUE), simulate);
        }
        
        public long insert(long maxInsert, boolean simulate) {
            return forgeStorage.receiveEnergy((int) Math.min(maxInsert, Integer.MAX_VALUE), simulate);
        }
        
        public IEnergyStorage getForgeStorage() {
            return forgeStorage;
        }
    }
}