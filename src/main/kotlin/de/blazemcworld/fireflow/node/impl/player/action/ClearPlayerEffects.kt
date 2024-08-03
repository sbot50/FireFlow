package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.PlayerType
import de.blazemcworld.fireflow.node.SignalType
import net.minestom.server.item.Material

object ClearPlayerEffects : BaseNode("Clear Player Effects", Material.MILK_BUCKET) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).clearEffects()
            }
            eval.emit(ctx[next])
        }
    }
}