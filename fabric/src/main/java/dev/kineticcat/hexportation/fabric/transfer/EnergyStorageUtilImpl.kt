package dev.kineticcat.hexportation.fabric.transfer

import dev.kineticcat.hexportation.api.transfer.EnergyStorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction as FabricTransaction

/**
 * Fabric implementation of our cross-platform EnergyStorageUtil.
 * Delegates to Team Reborn Energy's actual EnergyStorageUtil.move() method.
 */
class EnergyStorageUtilImpl : EnergyStorageUtil.Implementation() {
    
    companion object {
        @JvmField
        val INSTANCE = EnergyStorageUtilImpl()
    }
    
    /**
     * Implementation of EnergyStorageUtil.move() that delegates to Team Reborn Energy's util.
     * This preserves the exact same behavior as the original code.
     */
    override fun move(source: Any?, sink: Any?, maxAmount: Long, transaction: Any?): Long {
        // Extract Team Reborn Energy objects from our wrappers
        val sourceWrapper = source as EnergyStorageImpl.FabricEnergyWrapper
        val sinkWrapper = sink as EnergyStorageImpl.FabricEnergyWrapper
        
        // Cast back to Team Reborn Energy types and delegate to the real EnergyStorageUtil
        return team.reborn.energy.api.EnergyStorageUtil.move(
            sourceWrapper.getTeamRebornStorage(), 
            sinkWrapper.getTeamRebornStorage(), 
            maxAmount,
            transaction as? FabricTransaction
        )
    }
}