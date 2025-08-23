package dev.kineticcat.hexportation.api.transfer

/**
 * Cross-platform replacement for Team Reborn Energy's EnergyStorageUtil.
 * Provides identical method signatures so existing code needs zero changes.
 * Just the import changes from team.reborn.energy.api.EnergyStorageUtil
 * to dev.kineticcat.hexportation.api.transfer.EnergyStorageUtil
 */
object EnergyStorageUtil {
    
    private var implementation: Implementation? = null
    
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
    @JvmStatic
    fun move(source: Any?, sink: Any?, maxAmount: Long, transaction: Any?): Long {
        return implementation?.move(source, sink, maxAmount, transaction) 
            ?: throw IllegalStateException("EnergyStorageUtil not initialized - platform implementation missing")
    }
    
    abstract class Implementation {
        abstract fun move(source: Any?, sink: Any?, maxAmount: Long, transaction: Any?): Long
    }
    
    // Platform modules will call this to inject their implementation
    @JvmStatic
    fun setImplementation(impl: Implementation) {
        implementation = impl
    }
}