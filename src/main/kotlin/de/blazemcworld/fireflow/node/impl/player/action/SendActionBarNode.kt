package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object SendActionBarNode : BaseNode("Send Action Bar", Material.BIRCH_HANGING_SIGN) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val message = input("Message", MessageType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = {
            val player = it[ctx[player]]?.resolve()
            val msg = it[ctx[message]]
            if (msg != null && player != null) player.sendActionBar(msg)
            it.emit(ctx[next])
        }
    }
}