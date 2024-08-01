package de.blazemcworld.fireflow.node.impl

import de.blazemcworld.fireflow.node.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.item.Material

object KillPlayerNode : BaseNode("Kill Player", Material.SKELETON_SKULL) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            eval[ctx[player]]?.resolve()?.kill()
            eval.emit(ctx[next])
        }
    }
}

object PlayerPositionNode : BaseNode("Player Position", Material.ENDER_EYE) {
    private val player = input("Player", PlayerType)
    private val position = output("Position", PositionType)

    override fun setup(ctx: NodeContext) {
        ctx[position].defaultHandler = { it[ctx[player]]?.resolve()?.position }
    }
}

object SendMessageNode : BaseNode("Send Message", Material.WRITTEN_BOOK) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val message = input("Message", MessageType, Component.text("Test"));
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = {
            val player = it[ctx[player]]?.resolve()
            val msg = it[ctx[message]]
            if (msg != null && player != null) player.sendMessage(msg)
            it.emit(ctx[next])
        }
    }
}