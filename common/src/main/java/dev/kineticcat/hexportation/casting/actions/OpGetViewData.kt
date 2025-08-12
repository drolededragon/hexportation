package dev.kineticcat.hexportation.fabric.casting.actions

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment
import at.petrak.hexcasting.api.casting.iota.Iota
import dev.kineticcat.hexportation.fabric.api.casting.iota.StorageViewIota.GenericStorageView
import dev.kineticcat.hexportation.fabric.api.getView
import java.util.function.Function

class OpGetViewData(val getter: Function<GenericStorageView, Iota>) : ConstMediaAction {
    override val argc: Int = 1
    override fun execute(args: List<Iota>, env: CastingEnvironment): List<Iota> {
        val view = args.getView(0)
        return listOf(getter.apply(view))
    }


}