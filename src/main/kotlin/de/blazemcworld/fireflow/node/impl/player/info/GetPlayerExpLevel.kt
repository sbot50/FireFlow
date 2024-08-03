package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import de.blazemcworld.fireflow.node.PlayerType
import net.minestom.server.item.Material

object GetPlayerExpLevel : BaseNode("Get Experience Level", Material.EXPERIENCE_BOTTLE) {
    private val player = input("Player", PlayerType)
    private val level = output("Level", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[level].defaultHandler = eval@{ eval ->
            run {
                return@eval (eval[ctx[player]]?.resolve() ?: return@run).level.toDouble()
            }
            null
        }
    }
}