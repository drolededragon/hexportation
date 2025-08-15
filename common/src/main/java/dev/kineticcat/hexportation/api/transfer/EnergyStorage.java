package dev.kineticcat.hexportation.api.transfer;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/**
 * Cross-platform replacement for Team Reborn Energy's EnergyStorage.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from team.reborn.energy.api.EnergyStorage
 * to dev.kineticcat.hexportation.api.transfer.EnergyStorage
 */
public interface EnergyStorage {
    public static final SidedStorage SIDED = new SidedStorage();
    
    /**
     * Get the amount of energy stored.
     * This method signature matches Team Reborn's EnergyStorage.getAmount()
     */
    long getAmount();
    
    /**
     * Get the capacity of this energy storage.
     * This method signature matches Team Reborn's EnergyStorage.getCapacity()
     */
    long getCapacity();
    
    public static class SidedStorage {
        @ExpectPlatform
        public EnergyStorage find(ServerLevel level, BlockPos pos, Direction direction) {
            throw new AssertionError("Platform implementation required");
        }
    }
}