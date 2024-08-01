package de.blazemcworld.fireflow.node.impl

import de.blazemcworld.fireflow.node.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.minestom.server.item.Material
import java.util.*

object FormatMiniMessageNode : BaseNode("Format MiniMessage", Material.INK_SAC) {
    private val message = input("Message", TextType)
    private val result = output("Result", MessageType)

    override fun setup(ctx: NodeContext) {
        ctx[result].defaultHandler = {
            val message = it[ctx[message]]!!
            MiniMessage.miniMessage().deserialize(message)
        }
    }
}

class ToMessageNode : GenericNode("To Message", Material.BOOK) {
    private val cache = WeakHashMap<ValueType<*>, Impl<*>>()
    override fun create(generics: Map<String, ValueType<*>>): Impl<*> = cache.computeIfAbsent(generics["Type"]) { Impl(generics["Type"]!!) }

    init {
        generic("Type", AllTypes.dataOnly)
        genericInput("Input")
        output("Output", MessageType)
    }

    class Impl<T>(val type: ValueType<T>) : BaseNode("To Message", type.material) {
        private val input = input("Input", type)
        private val output = output("Output", MessageType)
        override val generics = mapOf("Type" to type)

        override fun setup(ctx: NodeContext) {
            ctx[output].defaultHandler = {
                val input = ctx[input]
                if (input != null) { Component.text(type.stringify(it[input]!!)) } else { Component.empty() }
            }
        }
    }
}
