package dev.kineticcat.hexportation.fabric.casting.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.iota.ListIota
import dev.kineticcat.hexportation.fabric.api.Storage
import dev.kineticcat.hexportation.fabric.api.casting.iota.StorageViewIota
import dev.kineticcat.hexportation.fabric.api.getConduit
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView

class OpGetInvData(val sink: Boolean = false) : ConstMediaAction {
    override val argc = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val conduit = args.getConduit(0)
        val storage = Storage(conduit, env.world)

        return when (storage.mode) {
            Storage.Modes.ITEM -> {
                val iterator = storage.getItemIterator(sink)
                val total = mutableListOf<StorageViewIota>()
                iterator.forEach {view ->
                    total.add(StorageViewIota(view, 1L))
                }
                listOf(ListIota(total.asReversed() as List<Iota>))
            }
            Storage.Modes.FLUID -> {
                val iterator = storage.getFluidIterator(sink)
                val total = mutableListOf<StorageViewIota>()
                iterator.forEach {view ->
                    total.add(StorageViewIota(view, 1.0))
                }
                listOf(ListIota(total.asReversed() as List<Iota>))
            }
            Storage.Modes.ENERGY -> {
                listOf(ListIota(listOf(StorageViewIota(storage.getEnergyStorageOrNull(sink)))))
            }
            else -> listOf()
        }
    }
}