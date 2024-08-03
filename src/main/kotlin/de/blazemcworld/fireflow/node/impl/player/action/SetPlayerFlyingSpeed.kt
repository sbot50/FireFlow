package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object SetPlayerFlyingSpeed : BaseNode("Set Flying Speed", Material.ELYTRA) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val speed = input("Speed", NumberType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).flyingSpeed = (eval[ctx[speed]]?.toFloat() ?: return@run) / 20f
            }
            eval.emit(ctx[next])
        }
    }
}