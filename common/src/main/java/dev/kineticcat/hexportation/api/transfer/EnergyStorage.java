package dev.kineticcat.hexportation.api.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/**
 * Cross-platform replacement for Team Reborn Energy's EnergyStorage.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from team.reborn.energy.api.EnergyStorage
 * to dev.kineticcat.hexportation.api.transfer.EnergyStorage
 */
public abstract class EnergyStorage {
    public static SidedStorage SIDED;
    
    /**
     * Get the amount of energy stored.
     * This method signature matches Team Reborn's EnergyStorage.getAmount()
     */
    public abstract long getAmount();
    
    /**
     * Get the capacity of this energy storage.
     * This method signature matches Team Reborn's EnergyStorage.getCapacity()
     */
    public abstract long getCapacity();
    
    public static abstract class SidedStorage {
        public abstract EnergyStorage find(ServerLevel level, BlockPos pos, Direction direction);
    }
    
    // Platform modules will call this to inject their implementation
    public static void setSidedStorage(SidedStorage implementation) {
        SIDED = implementation;
    }
}