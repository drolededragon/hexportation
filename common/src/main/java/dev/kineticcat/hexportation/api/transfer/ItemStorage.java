package dev.kineticcat.hexportation.api.transfer;

import dev.architectury.injectables.annotations.ExpectPlatform;
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
    public static final SidedStorage SIDED = new SidedStorage();
    
    public static class SidedStorage {
        @ExpectPlatform
        public Storage<ItemVariant> find(ServerLevel level, BlockPos pos, Direction direction) {
            throw new AssertionError("Platform implementation required");
        }
    }
}