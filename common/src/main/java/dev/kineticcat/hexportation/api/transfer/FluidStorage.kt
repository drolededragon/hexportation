package dev.kineticcat.hexportation.api.transfer

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel

/**
 * Cross-platform replacement for Fabric's FluidStorage.SIDED.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage
 * to dev.kineticcat.hexportation.api.transfer.FluidStorage
 */
object FluidStorage {
    
    @JvmField
    var SIDED: SidedStorage? = null
    
    abstract class SidedStorage {
        abstract fun find(level: ServerLevel, pos: BlockPos, direction: Direction): Storage<FluidVariant>?
    }
    
    // Platform modules will call this to inject their implementation
    @JvmStatic
    fun setSidedStorage(implementation: SidedStorage) {
        SIDED = implementation
    }
}