package dev.kineticcat.hexportation.api.transfer

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel

/**
 * Cross-platform replacement for Fabric's ItemStorage.SIDED.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
 * to dev.kineticcat.hexportation.api.transfer.ItemStorage
 */
object ItemStorage {
    
    @JvmField
    var SIDED: SidedStorage? = null
    
    abstract class SidedStorage {
        abstract fun find(level: ServerLevel, pos: BlockPos, direction: Direction): Storage<ItemVariant>?
    }
    
    // Platform modules will call this to inject their implementation
    @JvmStatic
    fun setSidedStorage(implementation: SidedStorage) {
        SIDED = implementation
    }
}