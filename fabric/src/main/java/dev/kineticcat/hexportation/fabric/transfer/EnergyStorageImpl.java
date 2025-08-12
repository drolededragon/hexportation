package dev.kineticcat.hexportation.fabric.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/**
 * Fabric implementation that delegates to actual Team Reborn Energy EnergyStorage.SIDED.
 */
public class EnergyStorageImpl {
    
    public static Object find(ServerLevel level, BlockPos pos, Direction direction) {
        return team.reborn.energy.api.EnergyStorage.SIDED.find(level, pos, direction);
    }
}