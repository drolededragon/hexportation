package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.Storage;
import dev.kineticcat.hexportation.api.transfer.FluidVariant;
import dev.kineticcat.hexportation.api.transfer.FluidStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

/**
 * Fabric implementation that delegates to actual Fabric FluidStorage.SIDED.find().
 */
public class FluidStorageImpl {
    
    /**
     * Singleton instance that implements our SidedStorage interface
     */
    public static final FluidStorage.SidedStorage INSTANCE = new FluidStorage.SidedStorage() {
        @Override
        public Storage<FluidVariant> find(ServerLevel level, BlockPos pos, Direction direction) {
            // Cast the Fabric Storage to our interface - this works because both are interfaces
            return (Storage<FluidVariant>) net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage.SIDED.find(level, pos, direction);
        }
    };
}