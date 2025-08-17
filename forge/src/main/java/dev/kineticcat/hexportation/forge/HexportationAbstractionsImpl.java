package dev.kineticcat.hexportation.forge;

import dev.kineticcat.hexportation.HexportationAbstractions;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class HexportationAbstractionsImpl {
    /**
     * This is the actual implementation of {@link HexportationAbstractions#getConfigDirectory()}.
     */
    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }
	
    public static void initPlatformSpecific() {
        HexportationConfigForge.init();
    }
}
