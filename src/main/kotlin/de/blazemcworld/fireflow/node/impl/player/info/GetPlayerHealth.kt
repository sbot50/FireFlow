package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import de.blazemcworld.fireflow.node.PlayerType
import net.minestom.server.item.Material

object GetPlayerHealth : BaseNode("Get Health", Material.RED_DYE) {
    private val player = input("Player", PlayerType)
    private val health = output("Health", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[health].defaultHandler = { eval ->
            eval[ctx[player]]?.resolve()?.health?.toDouble() ?: 0.0
        }
    }
}