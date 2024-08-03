package de.blazemcworld.fireflow.node.impl.player.event

import de.blazemcworld.fireflow.node.*
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerChatEvent
import net.minestom.server.item.Material

object OnPlayerChatNode : BaseNode("On Player Chat", Material.PLAYER_HEAD) {
    private val signal = output("Signal", SignalType)
    private val player = output("Player", PlayerType)
    private val message = output("Message", TextType)

    override fun setup(ctx: NodeContext) {
        val node = EventNode.type("chat", EventFilter.INSTANCE)
        val events = ctx.global.space.playInstance.eventNode().addChild(node)

        node.addListener(PlayerChatEvent::class.java) {
            val evalCtx = EvaluationContext(ctx.global)
            evalCtx[ctx[player]] = { PlayerReference(it.player, ctx.global.space) }
            evalCtx[ctx[message]] = { it.message }
            evalCtx.emit(ctx[signal], now=true)
        }

        ctx.global.onDestroy += {
            events.removeChild(node)
        }
    }

}