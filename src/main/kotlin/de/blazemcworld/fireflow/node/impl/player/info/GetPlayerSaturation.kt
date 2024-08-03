package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import de.blazemcworld.fireflow.node.PlayerType
import net.minestom.server.item.Material

object GetPlayerSaturation : BaseNode("Get Saturation", Material.COOKED_BEEF) {
    private val player = input("Player", PlayerType)
    private val saturation = output("Saturation", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[saturation].defaultHandler = eval@{ eval ->
            run {
                return@eval (eval[ctx[player]]?.resolve() ?: return@run).foodSaturation.toDouble()
            }
            null
        }
    }
}