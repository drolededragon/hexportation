package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.Storage;
import dev.kineticcat.hexportation.api.transfer.ItemVariant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/**
 * Fabric implementation that delegates to actual Fabric ItemStorage.SIDED.find().
 */
public class ItemStorageImpl {
    
    public static Storage<ItemVariant> find(ServerLevel level, BlockPos pos, Direction direction) {
        // Cast the Fabric Storage to our interface - this works because both are interfaces
        return (Storage<ItemVariant>) net.fabricmc.fabric.api.transfer.v1.item.ItemStorage.SIDED.find(level, pos, direction);
    }
}