package dev.kineticcat.hexportation.fabric.casting.actions

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.getPositiveInt
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import dev.kineticcat.hexportation.fabric.api.*
import dev.kineticcat.hexportation.fabric.api.casting.iota.ConduitIota.Conduit
import net.minecraft.world.phys.Vec3

object OpSlurp : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val conduit = args.getConduit(0)
        var storage = OpenFrontedStorage(conduit, env.world)
        storage.mode ?: throw MishapInvalidIota.of(args[0], 0, "invalid_conduit")
        return SpellAction.Result(
            Spell(storage),
            if (env is CircleCastEnv) 0 else storage.cost() ?: 0L,
            listOf(
                ParticleSpray.burst(conduit.source.center, 0.1, 20),
                ParticleSpray(
                    conduit.sink.center.add(conduit.sinkDir.normal),
                    conduit.sinkDir.normal.asVec3().scale(-0.8),
                    0.1, 0.3, 20
                )
            )

        )
    }

    private data class Spell(val storage: OpenFrontedStorage) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            storage.move()
        }

    }
}