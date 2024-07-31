package de.blazemcworld.fireflow.node.impl

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import net.minestom.server.item.Material

object AddNumbersNode : BaseNode("Add Numbers", Material.SLIME_BLOCK) {
    private val left = input("Left", NumberType)
    private val right = input("Right", NumberType)
    private val result = output("Result", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[result].defaultHandler = {
            (it[ctx[left]] ?: 0.0) + (it[ctx[right]] ?: 0.0)
        }
    }
}

object SubtractNumbersNode : BaseNode("Subtract Numbers", Material.SLIME_BALL) {
    private val left = input("Left", NumberType)
    private val right = input("Right", NumberType)
    private val result = output("Result", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[result].defaultHandler = {
            (it[ctx[left]] ?: 0.0) - (it[ctx[right]] ?: 0.0)
        }
    }
}

object MultiplyNumbersNode : BaseNode("Multiply Numbers", Material.MAGMA_BLOCK) {
    private val left = input("Left", NumberType)
    private val right = input("Right", NumberType)
    private val result = output("Result", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[result].defaultHandler = {
            (it[ctx[left]] ?: 0.0) * (it[ctx[right]] ?: 0.0)
        }
    }
}

object DivideNumbersNode : BaseNode("Divide Numbers", Material.MAGMA_CREAM) {
    private val left = input("Left", NumberType)
    private val right = input("Right", NumberType)
    private val result = output("Result", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[result].defaultHandler = {
            (it[ctx[left]] ?: 0.0) / (it[ctx[right]] ?: 0.0)
        }
    }
}