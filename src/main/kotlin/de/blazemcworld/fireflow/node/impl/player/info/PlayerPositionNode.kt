package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.PlayerType
import de.blazemcworld.fireflow.node.PositionType
import net.minestom.server.item.Material

object PlayerPositionNode : BaseNode("Player Position", Material.ENDER_EYE) {
    private val player = input("Player", PlayerType)
    private val position = output("Position", PositionType)

    override fun setup(ctx: NodeContext) {
        ctx[position].defaultHandler = { it[ctx[player]]?.resolve()?.position }
    }
}