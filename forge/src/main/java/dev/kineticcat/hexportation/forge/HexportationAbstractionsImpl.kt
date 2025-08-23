package dev.kineticcat.hexportation.forge

import dev.kineticcat.hexportation.HexportationAbstractions
import net.minecraftforge.fml.loading.FMLPaths
import java.nio.file.Path

object HexportationAbstractionsImpl {
    /**
     * This is the actual implementation of [HexportationAbstractions.getConfigDirectory].
     */
    @JvmStatic
    fun getConfigDirectory(): Path = FMLPaths.CONFIGDIR.get()
	
    @JvmStatic
    fun initPlatformSpecific() = HexportationConfigForge.init()
}