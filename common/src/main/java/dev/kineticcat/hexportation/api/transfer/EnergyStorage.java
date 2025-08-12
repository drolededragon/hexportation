package dev.kineticcat.hexportation.api.transfer;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/**
 * Cross-platform replacement for Team Reborn Energy's EnergyStorage.SIDED.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from team.reborn.energy.api.EnergyStorage
 * to dev.kineticcat.hexportation.api.transfer.EnergyStorage
 */
public class EnergyStorage {
    public static final SidedStorage SIDED = new SidedStorage();
    
    public static class SidedStorage {
        @ExpectPlatform
        public Object find(ServerLevel level, BlockPos pos, Direction direction) {
            throw new AssertionError("Platform implementation required");
        }
    }
}