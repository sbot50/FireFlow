package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.PlayerType
import de.blazemcworld.fireflow.node.TextType
import net.minestom.server.item.Material

object GetPlayerName : BaseNode("Get Player Name", Material.NAME_TAG) {
    private val player = input("Player", PlayerType)
    private val name = output("Name", TextType)

    override fun setup(ctx: NodeContext) {
        ctx[name].defaultHandler = eval@{ eval ->
            run {
                return@eval (eval[ctx[player]]?.resolve() ?: return@run).username
            }
            null
        }
    }
}