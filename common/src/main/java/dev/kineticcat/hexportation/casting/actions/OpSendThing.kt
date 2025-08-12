package dev.kineticcat.hexportation.casting.actions

import at.petrak.hexcasting.api.casting.*
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import dev.kineticcat.hexportation.api.Storage
import dev.kineticcat.hexportation.api.add
import dev.kineticcat.hexportation.api.asVec3
import dev.kineticcat.hexportation.api.casting.iota.ConduitIota.Conduit
import dev.kineticcat.hexportation.api.getConduit
import dev.kineticcat.hexportation.api.transfer.StorageUtil

import team.reborn.energy.api.EnergyStorage
import dev.kineticcat.hexportation.api.transfer.EnergyStorageUtil


object OpSendThing : SpellAction {
    override val argc = 2

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        var conduit = args.getConduit(0)
        var amt = args.getPositiveInt(1).toLong()

        var storage = Storage(conduit, env.world)
        storage.mode ?: throw MishapInvalidIota.of(args[0], 0, "invalid_conduit")
        return SpellAction.Result(
                Spell(storage, amt),
                if (env is CircleCastEnv) 0 else storage.cost(amt) ?: 0,
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

    private data class Spell(var storage: Storage, var amt: Long) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            storage.move(amt)
        }
    }
}
