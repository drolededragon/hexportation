package dev.kineticcat.hexportation.fabric.casting.actions

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.getList
import at.petrak.hexcasting.api.casting.iota.DoubleIota
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import dev.kineticcat.hexportation.fabric.api.Storage
import dev.kineticcat.hexportation.fabric.api.add
import dev.kineticcat.hexportation.fabric.api.asVec3
import dev.kineticcat.hexportation.fabric.api.getConduit
import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
import team.reborn.energy.api.EnergyStorageUtil

object OpSendThingFiltered : SpellAction{
    override val argc: Int = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val conduit = args.getConduit(0)
        val list = args.getList(1)
        if (!list.all { iota -> iota is DoubleIota && DoubleIota.tolerates(iota.double % 1, 0.0) })
            throw MishapInvalidIota.of(args[1], 1, "non_int_list")
        val amounts = list.map { iota -> (iota as DoubleIota).double.toLong() }
        val storage = Storage(conduit, env.world)
        val cost = amounts.reduce { acc, l -> acc + (storage.cost(l) ?: 0) }
        storage.mode ?: throw MishapInvalidIota.of(args[0], 0, "invalid_conduit")
        return SpellAction.Result(
                Spell(storage, amounts),
                if (env is CircleCastEnv) 0 else cost,
                listOf(
                        ParticleSpray(
                                conduit.source.center.add(conduit.sourceDir.normal.asVec3().scale(.5)),
                                conduit.sourceDir.normal.asVec3().scale(0.8),
                                0.1, 0.3, 20
                        ),
                        ParticleSpray(
                                conduit.sink.center.add(conduit.sinkDir.normal),
                                conduit.sinkDir.normal.asVec3().scale(-0.8),
                                0.1, 0.3, 20
                        )
                )
        )
    }

    private data class Spell(var storage: Storage, var amounts: List<Long>) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            when (storage.mode) {
                Storage.Modes.ITEM -> {
                    val (source, sink) = storage.conduit.getItemStoragesOrNull(env.world)
                    val amts = amounts.iterator()
                    val iterator = storage.getItemIterator()
                    iterator.forEach {view ->
                        if (!amts.hasNext()) return
                        val amt = amts.next()
                        Transaction.openOuter().use { trans ->

                            // check how much can be extracted
                            val maxExtracted = StorageUtil.simulateExtract(view, view.resource, amt, trans)

                            trans.openNested().use { transferTransaction ->
                                // check how much can be inserted
                                val accepted: Long = sink.insert(view.resource, maxExtracted, transferTransaction)

                                // extract it, or rollback if the amounts don't match
                                if (view.extract(view.resource, accepted, transferTransaction) == accepted) {
                                    transferTransaction.commit()
                                }
                            }
                            trans.commit()
                        }
                    }
                }
                Storage.Modes.FLUID -> {
                    val (source, sink) = storage.conduit.getFluidStoragesOrNull(env.world)
                    val amts = amounts.iterator()
                    val iterator = storage.getFluidIterator()
                    iterator.forEach {view ->
                        if (!amts.hasNext()) return
                        val amt = amts.next()
                        Transaction.openOuter().use { trans ->

                            // check how much can be extracted
                            val maxExtracted = StorageUtil.simulateExtract(view, view.resource, amt, trans)

                            trans.openNested().use { transferTransaction ->
                                // check how much can be inserted
                                val accepted: Long = sink.insert(view.resource, maxExtracted, transferTransaction)

                                // extract it, or rollback if the amounts don't match
                                if (view.extract(view.resource, accepted, transferTransaction) == accepted) {
                                    transferTransaction.commit()
                                }
                            }
                            trans.commit()
                        }
                    }
                }
                Storage.Modes.ENERGY -> {
                    val (source, sink) = storage.conduit.getEnergyStoragesOrNull(env.world)
                    if (amounts.isEmpty()) return
                    EnergyStorageUtil.move(source, sink, amounts[0], null)
                }
                else -> {}
            }
        }
    }
}
