package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import de.blazemcworld.fireflow.node.PlayerType
import net.minestom.server.item.Material

object PlayerGetAdditionalHearts : BaseNode("Get Additional Hearts", Material.GOLDEN_APPLE) {
    private val player = input("Player", PlayerType)
    private val hearts = output("Hearts", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[hearts].defaultHandler = eval@{ eval ->
            run {
                return@eval (eval[ctx[player]]?.resolve() ?: return@run).additionalHearts.toDouble()
            }
            null
        }
    }
}