package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.PlayerType
import de.blazemcworld.fireflow.node.TextType
import net.minestom.server.item.Material

object GetGameModeNode : BaseNode("Get Player GameMode", Material.BIRCH_SIGN) {
    private val player = input("Player", PlayerType)
    private val gamemode = output("gamemode", TextType)

    override fun setup(ctx: NodeContext) {
        ctx[gamemode].defaultHandler = eval@{ eval ->
            run {
                return@eval (eval[ctx[player]]?.resolve() ?: return@run).gameMode.name.lowercase()
            }
            null
        }
    }
}