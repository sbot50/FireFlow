package de.blazemcworld.fireflow.node.impl

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material
import java.util.*

object ConcatNode : BaseNode("Concatenate", Material.TRIPWIRE_HOOK) {
    private val left = input("Left", TextType)
    private val right = input("Right", TextType)

    private val result = output("Result", TextType)

    override fun setup(ctx: NodeContext) {
        ctx[result].defaultHandler = { it[ctx[left]] + it[ctx[right]] }
    }
}

object SubtextNode : BaseNode("Subtext", Material.SHEARS) {
    private val text = input("Text", TextType)
    private val min = input("Min", NumberType, 0.0)
    private val max = input("Max", NumberType)

    private val result = output("Result", TextType)

    override fun setup(ctx: NodeContext) {
        ctx[result].defaultHandler = {
            val text = it[ctx[text]]
            val length = text?.length?.minus(1) ?: 0
            val min = it[ctx[min]]?.toInt() ?: 0
            val max = it[ctx[max]]?.toInt() ?: length

            text?.substring(if (0 > min) 0 else min, if (max > length) length else max) ?: ""
        }
    }
}

object ToTextNode : GenericNode("To Text", Material.STRING) {
    private val cache = WeakHashMap<ValueType<*>, Impl<*>>()
    override fun create(generics: Map<String, ValueType<*>>): Impl<*> = cache.computeIfAbsent(generics["Type"]) { Impl(generics["Type"]!!) }

    init {
        generic("Type", AllTypes.dataOnly)
        genericInput("Input")
        output("Output", TextType)
    }

    class Impl<T>(val type: ValueType<T>) : BaseNode("To Text", type.material) {
        private val input = input("Input", type)
        private val output = output("Output", TextType)
        override val generics = mapOf("Type" to type)

        override fun setup(ctx: NodeContext) {
            val input = ctx[input] ?: return
            ctx[output].defaultHandler = { type.stringify(it[input]!!) }
        }
    }
}
