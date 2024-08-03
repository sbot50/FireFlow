package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object SetPlayerExpLevel : BaseNode("Set Experience Level", Material.ENCHANTING_TABLE) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val level = input("Level", NumberType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).level = eval[ctx[level]]?.toInt() ?: return@run
            }
            eval.emit(ctx[next])
        }
    }
}