package de.blazemcworld.fireflow.node.impl.player.event

import de.blazemcworld.fireflow.node.*
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerEntityInteractEvent
import net.minestom.server.item.Material

object OnPlayerOtherInteraction : BaseNode("On Player Other Interaction", Material.CREEPER_HEAD) {
    private val signal = output("Signal", SignalType)
    private val player = output("Player", PlayerType)
    private val target = output("Target", PlayerType)
    private val mainhand = output("Main Hand", ConditionType)
    private val pos = output("Target Pos", PositionType)

    override fun setup(ctx: NodeContext) {
        val node = EventNode.type("entity interact", EventFilter.INSTANCE)
        val events = ctx.global.space.playInstance.eventNode().addChild(node)

        node.addListener(PlayerEntityInteractEvent::class.java) {
            if (it.target !is Player) return@addListener
            val evalCtx = EvaluationContext(ctx.global)
            evalCtx[ctx[player]] = { PlayerReference(it.player, ctx.global.space) }
            evalCtx[ctx[target]] = { PlayerReference(it.target as Player, ctx.global.space) }
            evalCtx[ctx[mainhand]] = { it.hand == Player.Hand.MAIN }
            evalCtx[ctx[pos]] = { Pos(it.interactPosition) }
            evalCtx.emit(ctx[signal], now=true)
        }

        ctx.global.onDestroy += {
            events.removeChild(node)
        }
    }

}