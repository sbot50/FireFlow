package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object SetPlayerHealth : BaseNode("Set Health", Material.RED_TERRACOTTA) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val health = input("Health", NumberType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).health = eval[ctx[health]]?.toFloat() ?: return@run
            }
            eval.emit(ctx[next])
        }
    }
}