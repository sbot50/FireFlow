package de.blazemcworld.fireflow.node.impl

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import net.minestom.server.item.Material
import kotlin.math.pow

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

object ModuloNumbersNode : BaseNode("Modulo Numbers", Material.BLAZE_POWDER) {
    private val left = input("Left", NumberType)
    private val right = input("Right", NumberType)
    private val result = output("Result", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[result].defaultHandler = {
            (it[ctx[left]] ?: 0.0) % (it[ctx[right]] ?: 0.0)
        }
    }
}

object PowerNumbersNode : BaseNode("Power Numbers", Material.BLAZE_ROD) {
    private val base = input("Base", NumberType)
    private val exponent = input("Exponent", NumberType)
    private val result = output("Result", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[result].defaultHandler = {
            (it[ctx[base]] ?: 0.0).pow(it[ctx[exponent]] ?: 0.0)
        }
    }
}

object RandomNumberNode : BaseNode("Random Number", Material.GLOWSTONE_DUST) {
    private val min = input("Min", NumberType, 0.0)
    private val max = input("Max", NumberType, 1.0)
    private val result = output("Result", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[result].defaultHandler = {
            val min = it[ctx[min]] ?: 0.0
            val max = it[ctx[max]] ?: 1.0
            min + (max - min) * Math.random()
        }
    }
}