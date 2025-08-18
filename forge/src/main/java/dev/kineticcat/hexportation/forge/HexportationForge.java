package dev.kineticcat.hexportation.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.kineticcat.hexportation.Hexportation;
import dev.kineticcat.hexportation.api.casting.iota.HexportationIotaTypes;
import dev.kineticcat.hexportation.casting.HexportationPatternRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * This is your loading entrypoint on forge, in case you need to initialize
 * something platform-specific.
 */
@Mod(Hexportation.MOD_ID)
public class HexportationForge {
    public HexportationForge() {
        // Submit our event bus to let architectury register our content on the right time
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(Hexportation.MOD_ID, bus);
        bus.addListener(HexportationClientForge::init);
        bus.addListener(this::commonSetup);
        Hexportation.init();
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            HexportationPatternRegistry.init();
            HexportationIotaTypes.init();
        });
    }
}
