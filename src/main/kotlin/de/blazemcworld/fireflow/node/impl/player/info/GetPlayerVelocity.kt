package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.PlayerType
import de.blazemcworld.fireflow.node.VectorType
import net.minestom.server.item.Material

object GetPlayerVelocity : BaseNode("Get Velocity", Material.DIAMOND_BOOTS) {
    private val player = input("Player", PlayerType)
    private val velocity = output("Velocity", VectorType)

    override fun setup(ctx: NodeContext) {
        ctx[velocity].defaultHandler = eval@{ eval ->
            run {
                return@eval (eval[ctx[player]]?.resolve() ?: return@run).velocity
            }
            null
        }
    }
}