package dev.kineticcat.hexportation.forge;

import dev.kineticcat.hexportation.HexportationClient;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/**
 * Forge client loading entrypoint.
 */
public class ComplexhexClientForge {
    public static void init(FMLClientSetupEvent event) {
        HexportationClient.init();
    }
}
