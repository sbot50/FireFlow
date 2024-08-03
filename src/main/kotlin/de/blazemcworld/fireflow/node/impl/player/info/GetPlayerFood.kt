package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import de.blazemcworld.fireflow.node.PlayerType
import net.minestom.server.item.Material

object GetPlayerFood : BaseNode("Get Food", Material.PORKCHOP) {
    private val player = input("Player", PlayerType)
    private val food = output("Food", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[food].defaultHandler = eval@{ eval ->
            run {
                return@eval (eval[ctx[player]]?.resolve() ?: return@run).food.toDouble()
            }
            null
        }
    }
}