package dev.kineticcat.hexportation.forge;

import dev.architectury.platform.forge.EventBuses;
import dev.kineticcat.hexportation.Hexportation;
import dev.kineticcat.hexportation.api.casting.iota.HexportationIotaTypes;
import dev.kineticcat.hexportation.casting.HexportationPatternRegistry;
import dev.kineticcat.hexportation.api.transfer.ItemStorage;
import dev.kineticcat.hexportation.api.transfer.FluidStorage;
import dev.kineticcat.hexportation.api.transfer.EnergyStorage;
import dev.kineticcat.hexportation.api.transfer.StorageUtil;
import dev.kineticcat.hexportation.api.transfer.FluidConstants;
import dev.kineticcat.hexportation.api.transfer.EnergyStorageUtil;
import dev.kineticcat.hexportation.forge.transfer.ItemStorageImpl;
import dev.kineticcat.hexportation.forge.transfer.FluidStorageImpl;
import dev.kineticcat.hexportation.forge.transfer.EnergyStorageImpl;
import dev.kineticcat.hexportation.forge.transfer.StorageUtilImpl;
import dev.kineticcat.hexportation.forge.transfer.EnergyStorageUtilImpl;
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
        // Initialize platform-specific transfer API implementations early
        FluidConstants.setBucket(1000L); // 1000 mB = 1 bucket in Forge
        FluidConstants.setIngot(1000L / 9); // 1/9 bucket ≈ 111 mB
        ItemStorage.setSidedStorage(ItemStorageImpl.INSTANCE);
        FluidStorage.setSidedStorage(FluidStorageImpl.INSTANCE);
        EnergyStorage.setSidedStorage(EnergyStorageImpl.INSTANCE);
        StorageUtil.setImplementation(StorageUtilImpl.INSTANCE);
        EnergyStorageUtil.setImplementation(EnergyStorageUtilImpl.INSTANCE);
        
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
