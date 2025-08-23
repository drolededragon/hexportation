package dev.kineticcat.hexportation.forge.transfer

import dev.kineticcat.hexportation.api.transfer.Storage
import dev.kineticcat.hexportation.api.transfer.StorageView
import dev.kineticcat.hexportation.api.transfer.FluidVariant
import dev.kineticcat.hexportation.api.transfer.FluidStorage
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraftforge.common.capabilities.ForgeCapabilities
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.FluidStack
import kotlin.math.min

/**
 * Forge implementation that delegates to Forge's IFluidHandler capability system.
 * Provides identical API to Fabric FluidStorage through wrapper pattern.
 */
object FluidStorageImpl {
    
    /**
     * Singleton instance that implements our SidedStorage interface
     */
    @JvmField
    val INSTANCE = object : FluidStorage.SidedStorage() {
        override fun find(level: ServerLevel, pos: BlockPos, direction: Direction): Storage<FluidVariant>? {
            val blockEntity = level.getBlockEntity(pos) ?: return null
            val capability = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, direction)
            return capability.map(::ForgeFluidWrapper).orElse(null)
        }
    }
    
    /**
     * Wrapper class that implements our Storage<FluidVariant> interface using Forge's IFluidHandler.
     * Follows the same pattern as ForgeItemWrapper.
     */
    class ForgeFluidWrapper(private val forgeHandler: IFluidHandler) : Storage<FluidVariant> {
        
        override fun insert(resource: FluidVariant, maxAmount: Long, transaction: Any?): Long {
            val stack = FluidStack(resource.fluid, min(maxAmount, Integer.MAX_VALUE.toLong()).toInt()).apply {
                resource.nbt?.let { tag = it }
            }
            return forgeHandler.fill(stack, IFluidHandler.FluidAction.EXECUTE).toLong()
        }
        
        override fun simulateInsert(resource: FluidVariant, maxAmount: Long, transaction: Any?): Long {
            val stack = FluidStack(resource.fluid, min(maxAmount, Integer.MAX_VALUE.toLong()).toInt()).apply {
                resource.nbt?.let { tag = it }
            }
            return forgeHandler.fill(stack, IFluidHandler.FluidAction.SIMULATE).toLong()
        }
        
        override fun extract(resource: FluidVariant, maxAmount: Long, transaction: Any?): Long {
            val toDrain = FluidStack(resource.fluid, min(maxAmount, Integer.MAX_VALUE.toLong()).toInt()).apply {
                resource.nbt?.let { tag = it }
            }
            val drained = forgeHandler.drain(toDrain, IFluidHandler.FluidAction.EXECUTE)
            return if (drained.isEmpty) 0 else drained.amount.toLong()
        }
        
        override fun simulateExtract(resource: FluidVariant, maxAmount: Long, transaction: Any?): Long {
            val toDrain = FluidStack(resource.fluid, min(maxAmount, Integer.MAX_VALUE.toLong()).toInt()).apply {
                resource.nbt?.let { tag = it }
            }
            val drained = forgeHandler.drain(toDrain, IFluidHandler.FluidAction.SIMULATE)
            return if (drained.isEmpty) 0 else drained.amount.toLong()
        }
        
        override fun iterator(): Iterator<StorageView<FluidVariant>> =
            (0 until forgeHandler.tanks).map { ForgeFluidTankView(forgeHandler, it) }.iterator()
        
        override fun nonEmptyIterator(): Iterator<StorageView<FluidVariant>> =
            (0 until forgeHandler.tanks)
                .filter { !forgeHandler.getFluidInTank(it).isEmpty }
                .map { ForgeFluidTankView(forgeHandler, it) }
                .iterator()
        
        fun getForgeHandler() = forgeHandler
    }
    
    /**
     * Wrapper for individual tanks that implements StorageView<FluidVariant>.
     * Represents one tank of the IFluidHandler as a Fabric-style StorageView.
     */
    class ForgeFluidTankView(private val handler: IFluidHandler, private val tank: Int) : StorageView<FluidVariant> {
        
        override val resource: FluidVariant get() = 
            FluidVariantImpl.fromFluidStack(handler.getFluidInTank(tank))
        
        override val amount: Long get() = handler.getFluidInTank(tank).amount.toLong()
        
        override val capacity: Long get() = handler.getTankCapacity(tank).toLong()
        
        override val isBlank: Boolean get() = handler.getFluidInTank(tank).isEmpty
        
        override fun simulateExtract(resource: FluidVariant, maxAmount: Long, transaction: Any?): Long {
            val tankStack = handler.getFluidInTank(tank)
            if (tankStack.isEmpty || tankStack.fluid != resource.fluid) return 0
            
            val toDrain = FluidStack(resource.fluid, min(maxAmount, Integer.MAX_VALUE.toLong()).toInt()).apply {
                resource.nbt?.let { tag = it }
            }
            
            val drained = handler.drain(toDrain, IFluidHandler.FluidAction.SIMULATE)
            return if (drained.isEmpty) 0 else drained.amount.toLong()
        }
        
        override fun extract(resource: FluidVariant, maxAmount: Long, transaction: Any?): Long {
            val tankStack = handler.getFluidInTank(tank)
            if (tankStack.isEmpty || tankStack.fluid != resource.fluid) return 0
            
            val toDrain = FluidStack(resource.fluid, min(maxAmount, Integer.MAX_VALUE.toLong()).toInt()).apply {
                resource.nbt?.let { tag = it }
            }
            
            val drained = handler.drain(toDrain, IFluidHandler.FluidAction.EXECUTE)
            return if (drained.isEmpty) 0 else drained.amount.toLong()
        }
    }
}