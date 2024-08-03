package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object SpacePlayersNode : BaseNode("Space Players", Material.KNOWLEDGE_BOOK) {
    private val players = output("Players", ListType.create(PlayerType))

    override fun setup(ctx: NodeContext) {
        ctx[players].defaultHandler = {
            ListReference(PlayerType, ctx.global.space.playInstance.players.map { PlayerReference(it, ctx.global.space) }.toMutableList())
        }
    }
}