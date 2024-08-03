package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object PlayerSetAdditionalHearts : BaseNode("Set Additional Hearts", Material.ENDER_PEARL) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val extrapoints = input("ExtraPoints", NumberType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).setAdditionalHearts(eval[ctx[extrapoints]]?.toFloat() ?: return@run)
            }
            eval.emit(ctx[next])
        }
    }
}