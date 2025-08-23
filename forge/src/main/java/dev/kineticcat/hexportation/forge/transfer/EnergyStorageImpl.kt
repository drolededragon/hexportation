package dev.kineticcat.hexportation.forge.transfer

import dev.kineticcat.hexportation.api.transfer.EnergyStorage
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.energy.IEnergyStorage
import kotlin.math.min

/**
 * Forge implementation that delegates to Forge's IEnergyStorage capability system.
 * Provides identical API to Team Reborn Energy through wrapper pattern.
 */
object EnergyStorageImpl {
    
    /**
     * Singleton instance that implements our SidedStorage interface
     */
    @JvmField
    val INSTANCE = object : EnergyStorage.SidedStorage() {
        override fun find(level: ServerLevel, pos: BlockPos, direction: Direction): EnergyStorage? {
            val blockEntity = level.getBlockEntity(pos) ?: return null
            val capability = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction)
            return capability.map(::ForgeEnergyWrapper).orElse(null)
        }
    }
    
    @JvmStatic
    fun getAmount(energyStorage: Any): Long = when (energyStorage) {
        is ForgeEnergyWrapper -> energyStorage.amount
        is IEnergyStorage -> energyStorage.energyStored.toLong()
        else -> 0
    }
    
    @JvmStatic
    fun getCapacity(energyStorage: Any): Long = when (energyStorage) {
        is ForgeEnergyWrapper -> energyStorage.capacity
        is IEnergyStorage -> energyStorage.maxEnergyStored.toLong()
        else -> 0
    }
    
    /**
     * Wrapper class that extends our EnergyStorage abstract class using Forge's IEnergyStorage.
     */
    class ForgeEnergyWrapper(
        private val forgeStorage: IEnergyStorage
    ) : EnergyStorage() {
        
        override val amount: Long get() = forgeStorage.energyStored.toLong()
        
        override val capacity: Long get() = forgeStorage.maxEnergyStored.toLong()
        
        fun simulateExtract(maxExtract: Long): Long = 
            forgeStorage.extractEnergy(min(maxExtract, Int.MAX_VALUE.toLong()).toInt(), true).toLong()
        
        fun simulateInsert(maxInsert: Long): Long = 
            forgeStorage.receiveEnergy(min(maxInsert, Int.MAX_VALUE.toLong()).toInt(), true).toLong()
        
        fun extract(maxExtract: Long, simulate: Boolean): Long = 
            forgeStorage.extractEnergy(min(maxExtract, Int.MAX_VALUE.toLong()).toInt(), simulate).toLong()
        
        fun insert(maxInsert: Long, simulate: Boolean): Long = 
            forgeStorage.receiveEnergy(min(maxInsert, Int.MAX_VALUE.toLong()).toInt(), simulate).toLong()
        
        fun getForgeStorage() = forgeStorage
    }
}