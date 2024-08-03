package de.blazemcworld.fireflow.node.impl.player.event

import de.blazemcworld.fireflow.node.*
import net.minestom.server.coordinate.Pos
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.player.PlayerBlockBreakEvent
import net.minestom.server.item.Material

object OnPlayerBreakBlockNode : BaseNode("On Player Break Block", Material.COBBLESTONE) {
    private val signal = output("Signal", SignalType)
    private val player = output("Player", PlayerType)
    private val pos = output("Block", PositionType)

    override fun setup(ctx: NodeContext) {
        val node = EventNode.type("block break", EventFilter.INSTANCE)
        val events = ctx.global.space.playInstance.eventNode().addChild(node)

        node.addListener(PlayerBlockBreakEvent::class.java) {
            val evalCtx = EvaluationContext(ctx.global)
            evalCtx[ctx[player]] = { PlayerReference(it.player, ctx.global.space) }
            evalCtx[ctx[pos]] = { Pos(it.blockPosition) }
            evalCtx.emit(ctx[signal], now=true)
        }

        ctx.global.onDestroy += {
            events.removeChild(node)
        }
    }

}