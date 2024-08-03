package de.blazemcworld.fireflow.node.impl.player.event

import de.blazemcworld.fireflow.node.*
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerStartSneakingEvent
import net.minestom.server.item.Material

object OnPlayerStartSneakingNode : BaseNode("On Player Start Sneaking", Material.GOLDEN_BOOTS) {
    private val signal = output("Signal", SignalType)
    private val player = output("Player", PlayerType)

    override fun setup(ctx: NodeContext) {
        val node = EventNode.type("sneak", EventFilter.INSTANCE)
        val events = ctx.global.space.playInstance.eventNode().addChild(node)

        node.addListener(PlayerStartSneakingEvent::class.java) {
            val evalCtx = EvaluationContext(ctx.global)
            evalCtx[ctx[player]] = { PlayerReference(it.player, ctx.global.space) }
            evalCtx.emit(ctx[signal], now=true)
        }

        ctx.global.onDestroy += {
            events.removeChild(node)
        }
    }

}