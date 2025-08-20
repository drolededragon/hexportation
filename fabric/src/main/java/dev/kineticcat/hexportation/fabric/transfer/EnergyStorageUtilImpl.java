package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.EnergyStorageUtil;
import team.reborn.energy.api.EnergyStorage;

/**
 * Fabric implementation of our cross-platform EnergyStorageUtil.
 * Delegates to Team Reborn Energy's actual EnergyStorageUtil.move() method.
 */
public class EnergyStorageUtilImpl extends EnergyStorageUtil.Implementation {
    
    public static final EnergyStorageUtilImpl INSTANCE = new EnergyStorageUtilImpl();
    
    /**
     * Implementation of EnergyStorageUtil.move() that delegates to Team Reborn Energy's util.
     * This preserves the exact same behavior as the original code.
     */
    @Override
    public long move(Object source, Object sink, long maxAmount, Object transaction) {
        // Extract Team Reborn Energy objects from our wrappers
        EnergyStorageImpl.FabricEnergyWrapper sourceWrapper = (EnergyStorageImpl.FabricEnergyWrapper) source;
        EnergyStorageImpl.FabricEnergyWrapper sinkWrapper = (EnergyStorageImpl.FabricEnergyWrapper) sink;
        
        // Cast back to Team Reborn Energy types and delegate to the real EnergyStorageUtil
        return team.reborn.energy.api.EnergyStorageUtil.move(
            sourceWrapper.getTeamRebornStorage(), 
            sinkWrapper.getTeamRebornStorage(), 
            maxAmount,
            (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
        );
    }
}