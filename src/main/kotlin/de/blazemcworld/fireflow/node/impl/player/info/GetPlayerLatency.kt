package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import de.blazemcworld.fireflow.node.PlayerType
import net.minestom.server.item.Material

object GetPlayerLatency : BaseNode("Get Latency", Material.ORANGE_DYE) {
    private val player = input("Player", PlayerType)
    private val latency = output("Latency", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[latency].defaultHandler = { eval ->
            eval[ctx[player]]?.resolve()?.latency?.toDouble() ?: 0.0
        }
    }
}