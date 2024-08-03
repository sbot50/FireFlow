package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object SetPlayerFireTicks : BaseNode("Set Fire Ticks", Material.FLINT_AND_STEEL) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val ticks = input("Ticks", NumberType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).fireTicks = eval[ctx[ticks]]?.toInt() ?: return@run
            }
            eval.emit(ctx[next])
        }
    }
}