package de.blazemcworld.fireflow.node.impl

import de.blazemcworld.fireflow.node.*
import net.minestom.server.item.Material
import java.util.*

class SetVariableNode(private val store: VariableStore) : GenericNode("Set ${store.type} Variable", Material.IRON_BLOCK) {
    private val cache = WeakHashMap<ValueType<*>, Impl<*>>()
    override fun create(generics: Map<String, ValueType<*>>): Impl<*> = cache.computeIfAbsent(generics["Type"]) { Impl(generics["Type"]!!, store, this) }

    init {
        generic("Type", AllTypes.dataOnly)
        input("Signal", SignalType)
        input("Name", TextType)
        genericInput("Value")
        output("Next", SignalType)
    }

    class Impl<T>(val type: ValueType<T>, private val store: VariableStore, override val generic: SetVariableNode) : BaseNode("Set ${store.type} ${type.name} Variable", type.material) {
        private val signal = input("Signal", SignalType)
        private val name = input("Name", TextType)
        private val value = input("Value", type)
        private val next = output("Next", SignalType)
        override val generics = mapOf("Type" to type)

        override fun setup(ctx: NodeContext) {
            ctx[signal].signalListener = {
                it[ctx[name]]?.let { n ->
                    store.setVariable(it, n, it[ctx[value]])
                }
                it.emit(ctx[next])
            }
        }
    }
}

class GetVariableNode(private val store: VariableStore) : GenericNode("Get ${store.type} Variable", Material.IRON_NUGGET) {
    private val cache = WeakHashMap<ValueType<*>, Impl<*>>()
    override fun create(generics: Map<String, ValueType<*>>): Impl<*> = cache.computeIfAbsent(generics["Type"]) { Impl(generics["Type"]!!, store, this) }

    init {
        generic("Type", AllTypes.dataOnly)
        input("Name", TextType)
        genericOutput("Value")
    }

    class Impl<T>(val type: ValueType<T>, private val store: VariableStore, override val generic: GetVariableNode) : BaseNode("Get ${store.type} ${type.name} Variable", type.material) {
        private val name = input("Name", TextType)
        private val value = output("Value", type)
        override val generics = mapOf("Type" to type)

        override fun setup(ctx: NodeContext) {
            ctx[value].defaultHandler = {
                var out: T? = null
                it[ctx[name]]?.let { n ->
                    out = type.validate(store.getVariable(it, n))
                }
                out
            }
        }
    }
}

object VariableNodes {
    val all = listOf(
        GetVariableNode(VariableStore.Local), GetVariableNode(VariableStore.Space),
        SetVariableNode(VariableStore.Local), SetVariableNode(VariableStore.Space),
    )
}

interface VariableStore {
    val type: String
    fun getVariable(ctx: EvaluationContext, name: String): Any?
    fun setVariable(ctx: EvaluationContext, name: String, value: Any?)

    object Local : VariableStore {
        override val type = "Local"

        override fun getVariable(ctx: EvaluationContext, name: String): Any? = ctx.varStore[name]

        override fun setVariable(ctx: EvaluationContext, name: String, value: Any?) {
            ctx.varStore[name] = value
        }
    }

    object Space : VariableStore {
        override val type = "Space"

        override fun getVariable(ctx: EvaluationContext, name: String): Any? = ctx.global.varStore[name]

        override fun setVariable(ctx: EvaluationContext, name: String, value: Any?) {
            ctx.global.varStore[name] = value
        }
    }
}