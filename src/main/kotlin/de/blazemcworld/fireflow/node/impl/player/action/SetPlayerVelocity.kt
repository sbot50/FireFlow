package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object SetPlayerVelocity : BaseNode("Set Velocity", Material.FEATHER) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val vector = input("Vector", VectorType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).velocity = eval[ctx[vector]] ?: return@run
            }
            eval.emit(ctx[next])
        }
    }
}