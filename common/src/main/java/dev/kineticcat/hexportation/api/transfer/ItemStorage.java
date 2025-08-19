package dev.kineticcat.hexportation.api.transfer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/**
 * Cross-platform replacement for Fabric's ItemStorage.SIDED.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
 * to dev.kineticcat.hexportation.api.transfer.ItemStorage
 */
public class ItemStorage {
    public static SidedStorage SIDED;
    
    public static abstract class SidedStorage {
        public abstract Storage<ItemVariant> find(ServerLevel level, BlockPos pos, Direction direction);
    }
    
    // Platform modules will call this to inject their implementation
    public static void setSidedStorage(SidedStorage implementation) {
        SIDED = implementation;
    }
}