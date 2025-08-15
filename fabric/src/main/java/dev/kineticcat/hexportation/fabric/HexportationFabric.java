package dev.kineticcat.hexportation.fabric;

import dev.kineticcat.hexportation.Hexportation;
import dev.kineticcat.hexportation.api.casting.iota.HexportationIotaTypes;
import dev.kineticcat.hexportation.casting.HexportationPatternRegistry;
import net.fabricmc.api.ModInitializer;

/**
 * This is your loading entrypoint on fabric(-likes), in case you need to initialize
 * something platform-specific.
 * <br/>
 * Since quilt can load fabric mods, you develop for two platforms in one fell swoop.
 * Feel free to check out the <a href="https://github.com/architectury/architectury-templates">Architectury templates</a>
 * if you want to see how to add quilt-specific code.
 */
public class HexportationFabric implements ModInitializer {
    @Override
    public void onInitialize() {

        Hexportation.init();
        HexportationPatternRegistry.init();
        HexportationIotaTypes.init();
    }
}
