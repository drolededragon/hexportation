package dev.kineticcat.hexportation.fabric

import dev.kineticcat.hexportation.HexportationAbstractions
import net.fabricmc.loader.api.FabricLoader
import java.nio.file.Path

object HexportationAbstractionsImpl {
    /**
     * This is the actual implementation of [HexportationAbstractions.getConfigDirectory].
     */
    @JvmStatic
    fun getConfigDirectory(): Path = FabricLoader.getInstance().configDir
	
    @JvmStatic
    fun initPlatformSpecific() = HexportationConfigFabric.init()
}