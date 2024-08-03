package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object SetPlayerExperience : BaseNode("Set Experience", Material.SLIME_BLOCK) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val experience = input("Experience", NumberType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).exp = eval[ctx[experience]]?.toFloat() ?: return@run
            }
            eval.emit(ctx[next])
        }
    }
}