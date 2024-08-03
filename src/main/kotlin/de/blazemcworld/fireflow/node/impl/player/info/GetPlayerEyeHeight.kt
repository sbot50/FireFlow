package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import de.blazemcworld.fireflow.node.PlayerType
import net.minestom.server.item.Material

object GetPlayerEyeHeight : BaseNode("Get Eye Height", Material.SPIDER_EYE) {
    private val player = input("Player", PlayerType)
    private val eyeHeight = output("Height", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[eyeHeight].defaultHandler = eval@{ eval ->
            run {
                return@eval (eval[ctx[player]]?.resolve() ?: return@run).eyeHeight
            }
            null
        }
    }
}