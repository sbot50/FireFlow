package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object SetPlayerCanFlyNode : BaseNode("Set Player Can Fly", Material.WIND_CHARGE) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val case = input("Can Fly", ConditionType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).isAllowFlying = eval[ctx[case]] ?: true
            }
            eval.emit(ctx[next])
        }
    }
}