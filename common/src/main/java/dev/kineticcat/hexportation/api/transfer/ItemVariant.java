package dev.kineticcat.hexportation.api.transfer;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.item.Item;

/**
 * Cross-platform replacement for Fabric's ItemVariant.
 * Provides identical API so existing code needs zero changes.
 * Just the import changes from net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
 * to dev.kineticcat.hexportation.api.transfer.ItemVariant
 */
public interface ItemVariant {
    
    /**
     * Create an ItemVariant from an Item.
     * This method signature matches Fabric's ItemVariant.of(Item)
     */
    @ExpectPlatform
    static ItemVariant of(Item item) {
        throw new AssertionError("Platform implementation required");
    }
}