package dev.kineticcat.hexportation.api.transfer

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel

/**
 * Cross-platform replacement for Team Reborn Energy's EnergyStorage.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from team.reborn.energy.api.EnergyStorage
 * to dev.kineticcat.hexportation.api.transfer.EnergyStorage
 */
abstract class EnergyStorage {
    
    /**
     * Get the amount of energy stored.
     * This method signature matches Team Reborn's EnergyStorage.getAmount()
     */
    abstract val amount: Long
    
    /**
     * Get the capacity of this energy storage.
     * This method signature matches Team Reborn's EnergyStorage.getCapacity()
     */
    abstract val capacity: Long
    
    abstract class SidedStorage {
        abstract fun find(level: ServerLevel, pos: BlockPos, direction: Direction): EnergyStorage?
    }
    
    companion object {
        @JvmField
        var SIDED: SidedStorage? = null
        
        // Platform modules will call this to inject their implementation
        @JvmStatic
        fun setSidedStorage(implementation: SidedStorage) {
            SIDED = implementation
        }
    }
}