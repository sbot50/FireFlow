package de.blazemcworld.fireflow.node.impl.player.info

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import de.blazemcworld.fireflow.node.PlayerType
import net.minestom.server.item.Material

object GetPlayerHeldSlot : BaseNode("Get Held Slot", Material.STONE) {
    private val player = input("Player", PlayerType)
    private val handSlot = output("Slot", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[handSlot].defaultHandler = eval@{ eval ->
            run {
                return@eval (eval[ctx[player]]?.resolve() ?: return@run).heldSlot.toDouble()
            }
            null
        }
    }
}