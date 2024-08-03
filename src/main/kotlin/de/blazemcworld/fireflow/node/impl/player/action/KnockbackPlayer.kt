package de.blazemcworld.fireflow.node.impl.player.action

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material

object KnockbackPlayer : BaseNode("Knockback Player", Material.PISTON) {
    private val signal = input("Signal", SignalType)
    private val player = input("Player", PlayerType)
    private val strength = input("Strength", NumberType)
    private val x = input("X", NumberType)
    private val z = input("Z", NumberType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = { eval ->
            run {
                (eval[ctx[player]]?.resolve() ?: return@run).takeKnockback(eval[ctx[strength]]?.toFloat() ?: return@run, eval[ctx[x]] ?: return@run, eval[ctx[z]] ?: return@run)
            }
            eval.emit(ctx[next])
        }
    }
}