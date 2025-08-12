package dev.kineticcat.hexportation.fabric.casting.actions

import at.petrak.hexcasting.api.casting.ParticleSpray
import at.petrak.hexcasting.api.casting.RenderedSpell
import at.petrak.hexcasting.api.casting.castables.SpellAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.misc.MediaConstants
import dev.architectury.event.events.client.ClientTooltipEvent.Render
import dev.kineticcat.hexportation.fabric.api.casting.iota.ConduitIota.Conduit
import dev.kineticcat.hexportation.fabric.api.getConduit
import net.minecraft.world.entity.Entity
import net.minecraft.world.phys.AABB

object OpTransplace : SpellAction {
    override val argc = 1

    override fun execute(args: List<Iota>, env: CastingEnvironment): SpellAction.Result {
        val conduit = args.getConduit(0)
        val victims = env.world.getEntities(null, AABB(conduit.source))
        return SpellAction.Result(
            Spell(victims, conduit),
            if (env is CircleCastEnv) 0 else MediaConstants.SHARD_UNIT * victims.size,
            listOf(
                ParticleSpray.burst(conduit.source.center, 0.1, 20),
                ParticleSpray.burst(conduit.sink.center, 0.1, 20)
            )
        )
    }

    data class Spell(val victims: List<Entity>, val conduit: Conduit) : RenderedSpell {
        override fun cast(env: CastingEnvironment) {
            val pos = conduit.sink.center
            for (victim in victims) victim.teleportTo(pos.x, pos.y, pos.z)
        }
    }
}