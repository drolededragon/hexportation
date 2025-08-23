package dev.kineticcat.hexportation.forge.transfer

import dev.kineticcat.hexportation.api.transfer.Storage
import dev.kineticcat.hexportation.api.transfer.StorageView
import dev.kineticcat.hexportation.api.transfer.ItemVariant
import dev.kineticcat.hexportation.api.transfer.ItemStorage
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.items.IItemHandler
import net.minecraft.world.item.ItemStack
import kotlin.math.min

/**
 * Forge implementation that delegates to Forge's IItemHandler capability system.
 * Provides identical API to Fabric ItemStorage through wrapper pattern.
 */
object ItemStorageImpl {
    
    /**
     * Singleton instance that implements our SidedStorage interface
     */
    @JvmField
    val INSTANCE = object : ItemStorage.SidedStorage() {
        override fun find(level: ServerLevel, pos: BlockPos, direction: Direction): Storage<ItemVariant>? {
            val blockEntity = level.getBlockEntity(pos) ?: return null
            val capability = blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction)
            return capability.map(::ForgeItemWrapper).orElse(null)
        }
    }
    
    /**
     * Wrapper class that implements our Storage<ItemVariant> interface using Forge's IItemHandler.
     * Follows the same pattern as ForgeEnergyWrapper.
     */
    class ForgeItemWrapper(
        private val forgeHandler: IItemHandler
    ) : Storage<ItemVariant> {
        
        override fun insert(resource: ItemVariant, maxAmount: Long, transaction: Any?): Long {
            val stack = resource.toStack(min(maxAmount, Int.MAX_VALUE.toLong()).toInt())
            
            // Try to insert into any available slot
            repeat(forgeHandler.slots) { slot ->
                if (forgeHandler.isItemValid(slot, stack)) {
                    val remaining = forgeHandler.insertItem(slot, stack, false)
                    val inserted = stack.count - remaining.count
                    if (inserted > 0) return inserted.toLong()
                }
            }
            return 0
        }
        
        override fun simulateInsert(resource: ItemVariant, maxAmount: Long, transaction: Any?): Long {
            val stack = resource.toStack(min(maxAmount, Int.MAX_VALUE.toLong()).toInt())
            
            // Simulate insertion into any available slot
            repeat(forgeHandler.slots) { slot ->
                if (forgeHandler.isItemValid(slot, stack)) {
                    val remaining = forgeHandler.insertItem(slot, stack, true)
                    val inserted = stack.count - remaining.count
                    if (inserted > 0) return inserted.toLong()
                }
            }
            return 0
        }
        
        override fun extract(resource: ItemVariant, maxAmount: Long, transaction: Any?): Long {
            // Try to extract from any slot containing this resource
            repeat(forgeHandler.slots) { slot ->
                val slotStack = forgeHandler.getStackInSlot(slot)
                if (!slotStack.isEmpty && ItemVariant.of(slotStack.item) == resource) {
                    val extracted = forgeHandler.extractItem(slot, min(maxAmount, Int.MAX_VALUE.toLong()).toInt(), false)
                    return extracted.count.toLong()
                }
            }
            return 0
        }
        
        override fun simulateExtract(resource: ItemVariant, maxAmount: Long, transaction: Any?): Long {
            // Try to simulate extraction from any slot containing this resource
            repeat(forgeHandler.slots) { slot ->
                val slotStack = forgeHandler.getStackInSlot(slot)
                if (!slotStack.isEmpty && ItemVariant.of(slotStack.item) == resource) {
                    val extracted = forgeHandler.extractItem(slot, min(maxAmount, Int.MAX_VALUE.toLong()).toInt(), true)
                    return extracted.count.toLong()
                }
            }
            return 0
        }
        
        override fun iterator(): Iterator<StorageView<ItemVariant>> =
            (0 until forgeHandler.slots).map { ForgeItemSlotView(forgeHandler, it) }.iterator()
        
        override fun nonEmptyIterator(): Iterator<StorageView<ItemVariant>> =
            (0 until forgeHandler.slots)
                .filter { !forgeHandler.getStackInSlot(it).isEmpty }
                .map { ForgeItemSlotView(forgeHandler, it) }.iterator()
        
        fun getForgeHandler() = forgeHandler
    }
    
    /**
     * Wrapper for individual slots that implements StorageView<ItemVariant>.
     * Represents one slot of the IItemHandler as a Fabric-style StorageView.
     */
    class ForgeItemSlotView(
        private val handler: IItemHandler,
        private val slot: Int
    ) : StorageView<ItemVariant> {
        
        override val resource: ItemVariant get() = 
            handler.getStackInSlot(slot).let { stack ->
                if (stack.isEmpty) ItemVariant.blank() else ItemVariant.of(stack.item)
            }
        
        override val amount: Long get() = handler.getStackInSlot(slot).count.toLong()
        
        override val capacity: Long get() = handler.getSlotLimit(slot).toLong()
        
        override val isBlank: Boolean get() = handler.getStackInSlot(slot).isEmpty
        
        override fun simulateExtract(resource: ItemVariant, maxAmount: Long, transaction: Any?): Long {
            val slotStack = handler.getStackInSlot(slot)
            if (slotStack.isEmpty || ItemVariant.of(slotStack.item) != resource) return 0
            
            val extracted = handler.extractItem(slot, min(maxAmount, Int.MAX_VALUE.toLong()).toInt(), true)
            return extracted.count.toLong()
        }
        
        override fun extract(resource: ItemVariant, maxAmount: Long, transaction: Any?): Long {
            val slotStack = handler.getStackInSlot(slot)
            if (slotStack.isEmpty || ItemVariant.of(slotStack.item) != resource) return 0
            
            val extracted = handler.extractItem(slot, min(maxAmount, Int.MAX_VALUE.toLong()).toInt(), false)
            return extracted.count.toLong()
        }
    }
}