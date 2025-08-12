package dev.kineticcat.hexportation.fabric.transfer;

import team.reborn.energy.api.EnergyStorage;

/**
 * Fabric implementation of our cross-platform EnergyStorageUtil.
 * Delegates to Team Reborn Energy's actual EnergyStorageUtil.move() method.
 */
public class EnergyStorageUtilImpl {
    
    /**
     * Implementation of EnergyStorageUtil.move() that delegates to Team Reborn Energy's util.
     * This preserves the exact same behavior as the original code.
     */
    public static long move(Object source, Object sink, long maxAmount, Object transaction) {
        // Cast back to Team Reborn Energy types and delegate to the real EnergyStorageUtil
        return team.reborn.energy.api.EnergyStorageUtil.move(
            (EnergyStorage) source, 
            (EnergyStorage) sink, 
            maxAmount,
            (net.fabricmc.fabric.api.transfer.v1.transaction.Transaction) transaction
        );
    }
}