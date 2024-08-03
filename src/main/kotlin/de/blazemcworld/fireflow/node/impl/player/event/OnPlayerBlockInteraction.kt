package de.blazemcworld.fireflow.node.impl.player.event

import de.blazemcworld.fireflow.node.*
import net.minestom.server.coordinate.Pos
import net.minestom.server.entity.Player
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockInteractEvent
import net.minestom.server.item.Material

object OnPlayerBlockInteraction : BaseNode("On Player Block Interaction", Material.COPPER_BLOCK) {
    private val signal = output("Signal", SignalType)
    private val player = output("Player", PlayerType)
    private val mainhand = output("Main Hand", ConditionType)
    private val pos = output("Target Pos", PositionType)

    override fun setup(ctx: NodeContext) {
        val node = EventNode.type("block interact", EventFilter.INSTANCE)
        val events = ctx.global.space.playInstance.eventNode().addChild(node)

        node.addListener(PlayerBlockInteractEvent::class.java) {
            val evalCtx = EvaluationContext(ctx.global)
            evalCtx[ctx[player]] = { PlayerReference(it.player, ctx.global.space) }
            evalCtx[ctx[mainhand]] = { it.hand == Player.Hand.MAIN }
            evalCtx[ctx[pos]] = { Pos(it.blockPosition.add(it.cursorPosition)) }
            evalCtx.emit(ctx[signal], now=true)
        }

        ctx.global.onDestroy += {
            events.removeChild(node)
        }
    }

}