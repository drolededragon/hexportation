package dev.kineticcat.hexportation.fabric;

import dev.kineticcat.hexportation.Hexportation;
import dev.kineticcat.hexportation.api.casting.iota.HexportationIotaTypes;
import dev.kineticcat.hexportation.casting.HexportationPatternRegistry;
import dev.kineticcat.hexportation.api.transfer.ItemStorage;
import dev.kineticcat.hexportation.api.transfer.FluidStorage;
import dev.kineticcat.hexportation.api.transfer.EnergyStorage;
import dev.kineticcat.hexportation.api.transfer.StorageUtil;
import dev.kineticcat.hexportation.api.transfer.FluidConstants;
import dev.kineticcat.hexportation.api.transfer.EnergyStorageUtil;
import dev.kineticcat.hexportation.api.transfer.FluidVariant;
import dev.kineticcat.hexportation.api.transfer.ItemVariant;
import dev.kineticcat.hexportation.api.transfer.Transaction;
import dev.kineticcat.hexportation.api.transfer.FluidVariantAttributes;
import dev.kineticcat.hexportation.fabric.transfer.ItemStorageImpl;
import dev.kineticcat.hexportation.fabric.transfer.FluidStorageImpl;
import dev.kineticcat.hexportation.fabric.transfer.EnergyStorageImpl;
import dev.kineticcat.hexportation.fabric.transfer.StorageUtilImpl;
import dev.kineticcat.hexportation.fabric.transfer.EnergyStorageUtilImpl;
import dev.kineticcat.hexportation.fabric.transfer.FluidVariantImpl;
import dev.kineticcat.hexportation.fabric.transfer.ItemVariantImpl;
import dev.kineticcat.hexportation.fabric.transfer.TransactionImpl;
import dev.kineticcat.hexportation.fabric.transfer.FluidVariantAttributesImpl;
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
        // Initialize platform-specific transfer API implementations
        FluidConstants.setBucket(net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.BUCKET);
        FluidConstants.setIngot(net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants.INGOT);
        ItemStorage.setSidedStorage(ItemStorageImpl.INSTANCE);
        FluidStorage.setSidedStorage(FluidStorageImpl.INSTANCE);
        EnergyStorage.setSidedStorage(EnergyStorageImpl.INSTANCE);
        StorageUtil.setImplementation(StorageUtilImpl.INSTANCE);
        EnergyStorageUtil.setImplementation(EnergyStorageUtilImpl.INSTANCE);
        
        // Initialize Variant implementations
        FluidVariant.setImplementation(FluidVariantImpl.INSTANCE);
        ItemVariant.setImplementation(ItemVariantImpl.INSTANCE);
        
        // Initialize Transaction and other implementations
        Transaction.setImplementation(TransactionImpl.INSTANCE);
        FluidVariantAttributes.setImplementation(FluidVariantAttributesImpl.INSTANCE);

        Hexportation.init();
        HexportationPatternRegistry.init();
        HexportationIotaTypes.init();
    }
}
