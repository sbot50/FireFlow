package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object SetPlayerFood : BaseNode("Set Food", Material.BAKED_POTATO) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val food = input("Food", NumberType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).food = eval[ctx[food]]?.toInt() ?: return@run
            }
            eval.emit(ctx[next])
        }
    }
}