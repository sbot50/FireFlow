package de.blazemcworld.fireflow.node.impl.player.event

import de.blazemcworld.fireflow.node.*
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerStopFlyingWithElytraEvent
import net.minestom.server.item.Material

object OnPlayerStopElytraFlyingNode : BaseNode("On Stop Elytra Flying", Material.PHANTOM_MEMBRANE) {
    private val signal = output("Signal", SignalType)
    private val player = output("Player", PlayerType)

    override fun setup(ctx: NodeContext) {
        val node = EventNode.type("elytra", EventFilter.INSTANCE)
        val events = ctx.global.space.playInstance.eventNode().addChild(node)

        node.addListener(PlayerStopFlyingWithElytraEvent::class.java) {
            val evalCtx = EvaluationContext(ctx.global)
            evalCtx[ctx[player]] = { PlayerReference(it.player, ctx.global.space) }
            evalCtx.emit(ctx[signal], now=true)
        }

        ctx.global.onDestroy += {
            events.removeChild(node)
        }
    }

}