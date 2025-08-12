package dev.kineticcat.hexportation.fabric.transfer;

import dev.kineticcat.hexportation.api.transfer.ItemVariant;
import net.minecraft.world.item.Item;

/**
 * Fabric implementation that delegates to actual Fabric ItemVariant.of().
 */
public class ItemVariantImpl {
    
    public static ItemVariant of(Item item) {
        // Cast the Fabric ItemVariant to our interface - this works because both are interfaces
        return (ItemVariant) net.fabricmc.fabric.api.transfer.v1.item.ItemVariant.of(item);
    }
}