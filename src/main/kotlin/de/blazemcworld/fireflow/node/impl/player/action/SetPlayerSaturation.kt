package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object SetPlayerSaturation : BaseNode("Set Saturation", Material.COOKED_PORKCHOP) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val saturation = input("Saturation", NumberType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).foodSaturation = eval[ctx[saturation]]?.toFloat() ?: return@run
            }
            eval.emit(ctx[next])
        }
    }
}