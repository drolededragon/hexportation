package dev.kineticcat.hexportation.api.transfer;

/**
 * Cross-platform replacement for Team Reborn Energy's EnergyStorageUtil.
 * Provides identical method signatures so existing code needs zero changes.
 * Just the import changes from team.reborn.energy.api.EnergyStorageUtil
 * to dev.kineticcat.hexportation.api.transfer.EnergyStorageUtil
 */
public class EnergyStorageUtil {
    
    private static Implementation implementation;
    
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
    public static long move(Object source, Object sink, long maxAmount, Object transaction) {
        if (implementation == null) {
            throw new IllegalStateException("EnergyStorageUtil not initialized - platform implementation missing");
        }
        return implementation.move(source, sink, maxAmount, transaction);
    }
    
    public static abstract class Implementation {
        public abstract long move(Object source, Object sink, long maxAmount, Object transaction);
    }
    
    // Platform modules will call this to inject their implementation
    public static void setImplementation(Implementation impl) {
        implementation = impl;
    }
}