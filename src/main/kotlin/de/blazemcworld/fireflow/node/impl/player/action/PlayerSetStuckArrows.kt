package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object PlayerSetStuckArrows : BaseNode("Set Arrow Count", Material.CROSSBOW) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val arrows = input("Arrows", NumberType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).arrowCount = eval[ctx[arrows]]?.toInt() ?: return@run
            }
            eval.emit(ctx[next])
        }
    }
}