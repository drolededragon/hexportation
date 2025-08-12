package dev.kineticcat.hexportation.fabric.casting.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.getBlockPos
import at.petrak.hexcasting.api.casting.getVec3
import at.petrak.hexcasting.api.casting.iota.Iota
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
import at.petrak.hexcasting.api.misc.MediaConstants
import dev.kineticcat.hexportation.Hexportation
import dev.kineticcat.hexportation.fabric.api.Storage
import dev.kineticcat.hexportation.fabric.api.casting.iota.ConduitIota
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage
import net.minecraft.core.Direction
import org.joml.Vector3i

object OpMakeConduit : ConstMediaAction {
    override val argc: Int = 4
    override val mediaCost: Long = MediaConstants.QUENCHED_BLOCK_UNIT * 3
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val source = args.getBlockPos(0);
        val sourcedirvec = args.getBlockPos(1)
        val sourcedir = Direction.fromDelta(sourcedirvec.x, sourcedirvec.y, sourcedirvec.z)
        val sink = args.getBlockPos(2);
        val sinkdirvec = args.getBlockPos(3)
        val sinkdir = Direction.fromDelta(sinkdirvec.x, sinkdirvec.y, sinkdirvec.z)

        env.assertPosInRange(source)
        env.assertPosInRange(sink)
        return ConduitIota(source, sourcedir, sink, sinkdir).asActionResult();
    }
}