package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import de.blazemcworld.fireflow.node.PlayerType
import net.minestom.server.item.Material

object GetPlayerFireTicks : BaseNode("Get Fire Ticks", Material.LAVA_BUCKET) {
    private val player = input("Player", PlayerType)
    private val ticks = output("Ticks", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[ticks].defaultHandler = eval@{ eval ->
            run {
                return@eval (eval[ctx[player]]?.resolve() ?: return@run).fireTicks.toDouble()
            }
            null
        }
    }
}