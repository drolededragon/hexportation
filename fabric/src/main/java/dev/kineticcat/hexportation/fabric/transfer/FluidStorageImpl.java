package dev.kineticcat.hexportation.fabric.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/**
 * Fabric implementation that delegates to actual Fabric FluidStorage.SIDED.
 */
public class FluidStorageImpl {
    
    public static Object find(ServerLevel level, BlockPos pos, Direction direction) {
        return net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage.SIDED.find(level, pos, direction);
    }
}