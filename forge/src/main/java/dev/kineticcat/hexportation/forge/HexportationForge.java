package dev.kineticcat.hexportation.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.kineticcat.hexportation.Hexportation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * This is your loading entrypoint on forge, in case you need to initialize
 * something platform-specific.
 */
@Mod(Hexportation.MOD_ID)
public class ComplexhexForge {
    public ComplexhexForge() {
        // Submit our event bus to let architectury register our content on the right time
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Hexportation.MOD_ID, bus);
        bus.addListener(ComplexhexClientForge::init);
        Hexportation.init();
    }
}
