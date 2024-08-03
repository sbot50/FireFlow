package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import de.blazemcworld.fireflow.node.PlayerType
import net.minestom.server.item.Material

object PlayerGetStuckArrows : BaseNode("Get Stuck Arrows", Material.ARROW) {
    private val player = input("Player", PlayerType)
    private val arrows = output("Arrows", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[arrows].defaultHandler = eval@{ eval ->
            run {
                return@eval (eval[ctx[player]]?.resolve() ?: return@run).arrowCount.toDouble()
            }
            null
        }
    }
}