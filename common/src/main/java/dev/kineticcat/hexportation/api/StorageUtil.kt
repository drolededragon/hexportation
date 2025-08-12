@file:Suppress("UnstableApiUsage")

package dev.kineticcat.hexportation.fabric.api

import dev.kineticcat.hexportation.Hexportation
import dev.kineticcat.hexportation.fabric.api.casting.iota.ConduitIota.Conduit
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.LiquidBlock
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.phys.AABB
import team.reborn.energy.api.EnergyStorage
import team.reborn.energy.api.EnergyStorageUtil

class Storage(val conduit: Conduit, val sLevel: ServerLevel) {
    enum class Modes {
        ITEM, FLUID, ENERGY
    }
    val mode = getModeOrNull()

    fun cost(count: Long) = when (mode) {
        Modes.ITEM -> ITEM_COST(count)
        Modes.FLUID -> FLUID_COST(count)
        Modes.ENERGY -> ENERGY_COST(count)
        else -> null
    }

    fun getModeOrNull(): Modes? {
        return when {
            conduit.getItemStoragesOrNull(sLevel).let {(source, sink) -> source != null && sink != null} -> Modes.ITEM
            conduit.getFluidStoragesOrNull(sLevel).let {(source, sink) -> source != null && sink != null} -> Modes.FLUID
            conduit.getEnergyStoragesOrNull(sLevel).let {(source, sink) -> source != null && sink != null} -> Modes.ENERGY
            else -> null
        }
    }
    fun move(amt: Long) {
        when (mode) {
            Modes.ITEM -> {
                val (source, sink) = conduit.getItemStoragesOrNull(sLevel)
                StorageUtil.move(source, sink, { true }, amt, null)
            }
            Modes.FLUID -> {
                val (source, sink) = conduit.getFluidStoragesOrNull(sLevel)
                StorageUtil.move(source, sink, { true }, amt, null)
            }
            Modes.ENERGY -> {
                val (source, sink) = conduit.getEnergyStoragesOrNull(sLevel)
                EnergyStorageUtil.move(source, sink, amt, null)
            }
            null -> {} // should probably throw but eh
        }
    }
    fun getEnergyStorageOrNull(sink: Boolean = false): EnergyStorage =
             if (sink) conduit.getEnergyStoragesOrNull(sLevel).second else conduit.getEnergyStoragesOrNull(sLevel).first
    fun getItemIterator(sink: Boolean = false): Iterator<StorageView<ItemVariant>> =
            (if (sink) conduit.getItemStoragesOrNull(sLevel).second else conduit.getItemStoragesOrNull(sLevel).first).iterator()
    fun getFluidIterator(sink: Boolean = false): Iterator<StorageView<FluidVariant>> =
            (if (sink) conduit.getFluidStoragesOrNull(sLevel).second else conduit.getFluidStoragesOrNull(sLevel).first).iterator()
}

class OpenFrontedStorage( val conduit: Conduit, val sLevel: ServerLevel) {

    enum class Modes {
        ITEM, FLUID
    }
    val mode = getModeOrNull()

    fun cost() = when (mode) {
        Modes.ITEM -> {
            val sink = conduit.getItemStoragesOrNull(sLevel).second
            if (sink.nonEmptyIterator().hasNext()) ITEM_COST(sink.nonEmptyIterator().next().amount) else null
        }
        Modes.FLUID -> FLUID_COST(FluidConstants.BUCKET)
        else -> null
    }

    fun getModeOrNull(): Modes? {
        return when {
            conduit.getItemStoragesOrNull(sLevel).let {(source, sink) -> source == null && sink != null} -> Modes.ITEM
            conduit.getFluidStoragesOrNull(sLevel).let {(source, sink) -> source == null && sink != null} -> Modes.FLUID
            else -> null
        }
    }

    fun move() {
        when (mode) {
            Modes.ITEM -> {
                val sink = conduit.getItemStoragesOrNull(sLevel).second
                val item = sLevel.getEntities(null, AABB(conduit.source)) { it is ItemEntity }
                    .map {it as ItemEntity}
                    .sortedBy { it.age }
                    .getOrNull(0)
                    ?: return
                Transaction.openOuter().use {
                    val actual = sink.insert(ItemVariant.of(item.item.item), item.item.count.toLong(), it)
                    item.item.count -= actual.toInt()
                    it.commit()
                }
            }
            Modes.FLUID -> {
                val sink = conduit.getFluidStoragesOrNull(sLevel).second
                val state = sLevel.getBlockState(conduit.source)
                val fstate = state.fluidState
                val waterlogged = state.hasProperty(BlockStateProperties.WATERLOGGED)
                if (fstate.isEmpty || !fstate.isSource) return
                Transaction.openOuter().use{
                    val actual = sink.insert(FluidVariant.of(fstate.type), FluidConstants.BUCKET, it)
                    if (actual < FluidConstants.BUCKET) it.abort()
                    if (waterlogged) {
                        sLevel.setBlockAndUpdate(conduit.source, state.setValue(BlockStateProperties.WATERLOGGED, false))
                    } else {
                        val newState = fstate.createLegacyBlock().setValue(LiquidBlock.LEVEL, 14)
                        sLevel.setBlockAndUpdate(conduit.source, newState)
                    }
                    it.commit()
                }

            }
            null -> {}
        }
    }
}

class OpenEndedStorage(val conduit: Conduit, val sLevel: ServerLevel) {
    enum class Modes {
        ITEM, FLUID
    }
    val mode = getModeOrNull()

    fun cost() = when (mode) {
        Modes.ITEM -> {
            val source = conduit.getItemStoragesOrNull(sLevel).first
            if (source.nonEmptyIterator().hasNext()) ITEM_COST(source.nonEmptyIterator().next().amount) else null
        }
        Modes.FLUID -> FLUID_COST(FluidConstants.BUCKET)
        else -> null
    }

    fun getModeOrNull(): Modes? {
        return when {
            conduit.getItemStoragesOrNull(sLevel).let { (source, sink) -> source != null && sink == null } -> Modes.ITEM
            conduit.getFluidStoragesOrNull(sLevel).let { (source, sink) -> source != null && sink == null } -> Modes.FLUID
            else -> null
        }
    }

    fun move() {
        when (mode) {
            Modes.ITEM -> {
                val source = conduit.getItemStoragesOrNull(sLevel).first
                Transaction.openOuter().use {outer ->
                    if (!source.nonEmptyIterator().hasNext()) outer.abort()
                    val view = source.nonEmptyIterator().next()
                    val resource = view.resource
                    val taken = source.extract(view.resource, view.amount, outer)
                    Block.popResourceFromFace(sLevel, conduit.sink, conduit.sinkDir.opposite, resource.toStack(taken.toInt()))
                    outer.commit()
                }
            }
            Modes.FLUID -> {
                val source = conduit.getFluidStoragesOrNull(sLevel).first
                Transaction.openOuter().use {
                    if (!source.nonEmptyIterator().hasNext()) it.abort()
                    val view = source.nonEmptyIterator().next()
                    val resource = view.resource
                    Hexportation.LOGGER.info(resource)
                    val taken = source.extract(view.resource, FluidConstants.BUCKET, it)
                    Hexportation.LOGGER.info(taken)
                    if (taken != FluidConstants.BUCKET) it.abort()

                    val state = sLevel.getBlockState(conduit.sink)
                    Hexportation.LOGGER.info(state)

                    when {
                        state.hasProperty(BlockStateProperties.WATERLOGGED) && resource.fluid == Fluids.WATER ->
                            sLevel.setBlockAndUpdate(conduit.sink, state.setValue(BlockStateProperties.WATERLOGGED, true))
                        state.isAir ->
                            sLevel.setBlockAndUpdate(conduit.sink, resource.fluid.defaultFluidState().createLegacyBlock())
                        else -> it.abort()
                    }
                    it.commit()
                }
            }
            null -> {}
        }
    }
}