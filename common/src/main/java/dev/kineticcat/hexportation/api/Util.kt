package dev.kineticcat.hexportation.fabric.api

import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
import at.petrak.hexcasting.api.misc.MediaConstants
import dev.kineticcat.hexportation.fabric.api.casting.iota.ConduitIota
import dev.kineticcat.hexportation.fabric.api.casting.iota.ConduitIota.Conduit
import dev.kineticcat.hexportation.fabric.api.casting.iota.StorageViewIota
import dev.kineticcat.hexportation.fabric.api.casting.iota.StorageViewIota.GenericStorageView
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants
import net.minecraft.core.Vec3i
import net.minecraft.world.phys.Vec3
import kotlin.math.log

fun List<Iota>.getConduit(idx: Int, argc: Int = 0): Conduit {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is ConduitIota) {
        return x.conduit
    } else {
        throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "conduit")
    }
}
fun List<Iota>.getView(idx: Int, argc: Int = 0): GenericStorageView {
    val x = this.getOrElse(idx) { throw MishapNotEnoughArgs(idx + 1, this.size) }
    if (x is StorageViewIota) {
        return x.view
    } else {
        throw MishapInvalidIota.ofType(x, if (argc == 0) idx else argc - (idx + 1), "view")
    }
}

fun Vec3.add(other: Vec3i): Vec3 {
    return this.add(other.x.toDouble(), other.y.toDouble(), other.z.toDouble())
}
fun Vec3i.asVec3(): Vec3 {
    return Vec3(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
}

fun ITEM_COST(count: Long) = ((if (count < 64) count.toDouble() else log(count.toDouble(), 1.06714)) * MediaConstants.DUST_UNIT).toLong()
fun FLUID_COST(count: Long) = ITEM_COST(count / FluidConstants.INGOT)
fun ENERGY_COST(count: Long) = ITEM_COST(count / 1000)

