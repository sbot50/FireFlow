package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import de.blazemcworld.fireflow.node.PlayerType
import net.minestom.server.item.Material

object GetPlayerExperience : BaseNode("Get Experience Points", Material.SLIME_BALL) {
    private val player = input("Player", PlayerType)
    private val exp = output("Experience", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[exp].defaultHandler = eval@{ eval ->
            run {
                return@eval (eval[ctx[player]]?.resolve() ?: return@run).exp.toDouble()
            }
            null
        }
    }
}