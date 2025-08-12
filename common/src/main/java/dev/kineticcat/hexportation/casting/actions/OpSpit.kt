package dev.kineticcat.hexportation.fabric.casting.actions

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.getInt
import at.petrak.hexcasting.api.casting.getPositiveInt
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import dev.kineticcat.hexportation.Hexportation
import dev.kineticcat.hexportation.fabric.api.OpenEndedStorage
import dev.kineticcat.hexportation.fabric.api.add
import dev.kineticcat.hexportation.fabric.api.asVec3
import dev.kineticcat.hexportation.fabric.api.getConduit
import net.minecraft.world.entity.item.ItemEntity

object OpSpit : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val conduit = args.getConduit(0)
        val storage = OpenEndedStorage(conduit, env.world)
        storage.mode ?: throw MishapInvalidIota.of(args[0], 0, "invalid_conduit")
        return SpellAction.Result(
            Spell(storage),
            if (env is CircleCastEnv) 0 else storage.cost() ?: 0,
            listOf(
                ParticleSpray(
                    conduit.source.center.add(conduit.sourceDir.normal.asVec3().scale(.5)),
                    conduit.sourceDir.normal.asVec3().scale(0.8),
                    0.1, 0.3, 20
                ),
                ParticleSpray.burst(conduit.sink.center, 0.1, 20)
            )
        )
    }

    data class Spell(val storage: OpenEndedStorage) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            storage.move()
        }

    }
}