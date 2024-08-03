package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.entity.GameMode
import net.minestom.server.item.Material

object SetGameModeNode : BaseNode("Set Player GameMode", Material.BEDROCK) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val gamemode = input("GameMode", TextType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                val name = eval[ctx[gamemode]]?.uppercase()
                (eval[ctx[player]]?.resolve() ?: return@run).gameMode = GameMode.entries.find { it.name == name } ?: return@run
            }
            eval.emit(ctx[next])
        }
    }
}