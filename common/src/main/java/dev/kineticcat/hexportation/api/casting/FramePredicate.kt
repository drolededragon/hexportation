//package dev.kineticcat.hexportation.fabric.api.casting
//
//import at.petrak.hexcasting.api.casting.SpellList
//import at.petrak.hexcasting.api.casting.eval.CastResult
//import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType
//import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect
//import at.petrak.hexcasting.api.casting.eval.vm.CastingVM
//import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame
//import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate
//import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation
//import at.petrak.hexcasting.api.casting.iota.DoubleIota
//import at.petrak.hexcasting.api.casting.iota.Iota
//import at.petrak.hexcasting.api.casting.iota.ListIota
//import at.petrak.hexcasting.api.casting.mishaps.Mishap
//import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota
//import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs
//import at.petrak.hexcasting.api.utils.NBTBuilder
//import at.petrak.hexcasting.api.utils.serializeToNBT
//import at.petrak.hexcasting.common.lib.hex.HexEvalSounds
//import dev.kineticcat.hexportation.Hexportation
//import dev.kineticcat.hexportation.fabric.api.casting.iota.ConduitIota
//import dev.kineticcat.hexportation.fabric.casting.HexportationPatternRegistry
//import dev.kineticcat.hexportation.fabric.casting.actions.OpSendThingPredicate
//import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant
//import net.fabricmc.fabric.api.transfer.v1.storage.StorageUtil
//import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction
//import net.minecraft.nbt.CompoundTag
//import net.minecraft.nbt.StringTag
//import net.minecraft.server.level.ServerLevel
//import ram.talia.moreiotas.api.casting.iota.StringIota
//
//data class FramePredicate(
//        val conduit: ConduitIota.Conduit,
//        val idx: Int,
//        val mode: OpSendThingPredicate.MODES,
//        val code: SpellList,
//        val baseStack: List<Iota>?,
//) : ContinuationFrame {
//    override val type = TYPE
//
//    override fun breakDownwards(stack: List<Iota>): Pair<Boolean, List<Iota>> {
//        TODO("Not yet implemented")
//    }
//
//    override fun evaluate(
//            continuation: SpellContinuation,
//            level: ServerLevel,
//            vm: CastingVM): CastResult {
//        Hexportation.LOGGER.info(idx)
//        var top: Iota? = null
//        val stack = if (baseStack == null) {
//            vm.image.stack.toList()
//        } else {
//            top = vm.image.stack.lastOrNull()
//            if (top == null || top !is DoubleIota) {
//                return CastResult(
//                        ListIota(code),
//                        continuation,
//                        null,
//                        listOf(OperatorSideEffect.DoMishap(
//                                if (top != null) MishapInvalidIota.of(top, 0, "pred_res") else MishapNotEnoughArgs(1, 0),
//                                Mishap.Context(HexportationPatternRegistry.SEND_ITEM_PRED, null))),
//                        ResolvedPatternType.ERRORED,
//                        HexEvalSounds.MISHAP
//                )
//            }
//            baseStack
//        }
//
//        top = (top ?: DoubleIota(1.0)) as DoubleIota
//        Hexportation.LOGGER.info(top.double)
//        val (source, sink) = conduit.getItemStoragesOrNull(level)
//        val views = source.iterator()
//        if (idx > 0) for (i in 0 until idx) views.next()
//
//        val (addToStack, newImage, newCont) = if (views.hasNext()) {
//            var view = views.next()
//            Hexportation.LOGGER.info(view)
//            if (idx > 0) {
//                if (!view.resource.equals(ItemVariant.blank())) {
//                    Transaction.openOuter().use { trans ->
//
//                        // check how much can be extracted
//                        val maxExtracted = StorageUtil.simulateExtract(view, view.resource, top.double.toLong(), trans)
//
//                        trans.openNested().use { transferTransaction ->
//                            // check how much can be inserted
//                            val accepted: Long = sink.insert(view.resource, maxExtracted, transferTransaction)
//
//                            // extract it, or rollback if the amounts don't match
//                            if (view.extract(view.resource, accepted, transferTransaction) == accepted) {
//                                transferTransaction.commit()
//                            }
//                        }
//                        trans.commit()
//                    }
//                }
//            }
//            val cont2 = continuation
//                    .pushFrame(FramePredicate(conduit, idx+1, mode, code, stack))
//                    .pushFrame(FrameEvaluate(code, true))
//            Triple(ListIota(listOf(StringIota.make(view.resource.item.toString()), DoubleIota(view.amount.toDouble()), DoubleIota(idx.toDouble()+1))),vm.image.withUsedOp(), cont2)
//        } else {
//            Triple(null, vm.image, continuation)
//        }
//
//        val tstack = stack.toMutableList()
//        if (addToStack != null) tstack.add(addToStack)
//        return CastResult(
//                ListIota(code),
//                newCont,
//                newImage.withResetEscape().copy(stack = tstack),
//                listOf(),
//                ResolvedPatternType.EVALUATED,
//                HexEvalSounds.THOTH
//        )
//    }
//
//    override fun serializeToNBT() = NBTBuilder {
//        "conduit" %= conduit.serialise()
//        "mode" %= StringTag.valueOf(mode.name)
//        "code" %= code.serializeToNBT()
//        if (baseStack != null) "base" %= baseStack.serializeToNBT()
//    }
//
//    override fun size() = code.size() + (baseStack?.size ?: 0) // i really hate iterators...
//
//
//    companion object {
//        val TYPE: ContinuationFrame.Type<FramePredicate> = object : ContinuationFrame.Type<FramePredicate> {
//            override fun deserializeFromNBT(tag: CompoundTag, world: ServerLevel): FramePredicate? {
//                TODO("Not yet implemented")
//            }
//        }
//    }
//}
//
//// Steps
//
///*
//1. pattern triggers first pred frame
//2. pred frame steps iterator and puts data on the stack
//3. schedule eval & pred
//4. eval frame does its stuff
//5. pred frame steps through iterator to get back to where it was, moves items, steps the iterator, and puts data on the stack
//6. goto 3
//*/