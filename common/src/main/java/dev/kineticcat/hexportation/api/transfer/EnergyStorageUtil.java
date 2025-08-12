package dev.kineticcat.hexportation.api.transfer;

import dev.architectury.injectables.annotations.ExpectPlatform;

/**
 * Cross-platform replacement for Team Reborn Energy's EnergyStorageUtil.
 * Provides identical method signatures so existing code needs zero changes.
 * Just the import changes from team.reborn.energy.api.EnergyStorageUtil
 * to dev.kineticcat.hexportation.api.transfer.EnergyStorageUtil
 */
public class EnergyStorageUtil {
    
    /**
     * Move energy from source to sink storage.
     * Exact same signature as Team Reborn Energy's EnergyStorageUtil.move()
     * 
     * @param source Source energy storage
     * @param sink Destination energy storage  
     * @param maxAmount Maximum amount to move
     * @param transaction Transaction object (usually null for auto-commit)
     * @return Amount actually moved
     */
    @ExpectPlatform
    public static long move(Object source, Object sink, long maxAmount, Object transaction) {
        throw new AssertionError("Platform implementation required");
    }
}