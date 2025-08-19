package dev.kineticcat.hexportation.fabric.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

import dev.kineticcat.hexportation.api.transfer.EnergyStorage;

/**
 * Fabric implementation that delegates to actual Team Reborn Energy EnergyStorage.
 */
public class EnergyStorageImpl {
    
    /**
     * Singleton instance that implements our SidedStorage interface
     */
    public static final EnergyStorage.SidedStorage INSTANCE = new EnergyStorage.SidedStorage() {
        @Override
        public EnergyStorage find(ServerLevel level, BlockPos pos, Direction direction) {
            // Cast the Team Reborn EnergyStorage to our interface - this works because both are interfaces
            return (EnergyStorage) team.reborn.energy.api.EnergyStorage.SIDED.find(level, pos, direction);
        }
    };
}