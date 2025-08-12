package dev.kineticcat.hexportation.fabric.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/**
 * Fabric implementation that delegates to actual Fabric ItemStorage.SIDED.
 */
public class ItemStorageImpl {
    
    public static Object find(ServerLevel level, BlockPos pos, Direction direction) {
        return net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.find(level, pos, direction);
    }
}