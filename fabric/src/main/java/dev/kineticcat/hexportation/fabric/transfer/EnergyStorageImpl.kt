package dev.kineticcat.hexportation.fabric.transfer

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import dev.kineticcat.hexportation.api.transfer.EnergyStorage
import team.reborn.energy.api.EnergyStorage as TeamRebornEnergyStorage

/**
 * Fabric implementation that delegates to actual Team Reborn Energy EnergyStorage.
 */
object EnergyStorageImpl {
    
    /**
     * Singleton instance that implements our SidedStorage interface
     */
    @JvmField
    val INSTANCE = object : EnergyStorage.SidedStorage() {
        override fun find(level: ServerLevel, pos: BlockPos, direction: Direction): EnergyStorage? {
            val teamRebornStorage = TeamRebornEnergyStorage.SIDED.find(level, pos, direction)
                ?: return null
            
            return FabricEnergyWrapper(teamRebornStorage)
        }
    }
    
    /**
     * Wrapper class that extends our EnergyStorage abstract class using Team Reborn Energy.
     */
    class FabricEnergyWrapper(
        private val teamRebornStorage: TeamRebornEnergyStorage
    ) : EnergyStorage() {
        
        override val amount: Long get() = teamRebornStorage.amount
        
        override val capacity: Long get() = teamRebornStorage.capacity
        
        fun getTeamRebornStorage() = teamRebornStorage
    }
}