package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object SetPlayerHeldSlot : BaseNode("Set Held Slot", Material.SMOOTH_STONE) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val slot = input("Slot", NumberType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).setHeldItemSlot(eval[ctx[slot]]?.toInt()?.toByte() ?: return@run)
            }
            eval.emit(ctx[next])
        }
    }
}