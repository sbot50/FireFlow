package de.blazemcworld.fireflow.node

import de.blazemcworld.fireflow.gui.NodeComponent
import java.util.*
import java.util.function.Supplier

class EvaluationContext(val global: GlobalNodeContext, val varStore: MutableMap<String, Any?> = mutableMapOf()) {
    private val store = mutableMapOf<NodeContext.BoundOutput<*>, Supplier<*>>()
    private val tasks = Stack<Runnable>()
    val functionStack = Stack<NodeComponent>()

    operator fun <T> set(output: NodeContext.BoundOutput<T>, value: Supplier<T>) {
        store[output] = value
    }

    operator fun <T> get(input: NodeContext.BoundInput<T>): T? {
        var out: T? = null
        global.measureCode {
            if (input is NodeContext.BoundInsetInput && input.insetVal != null) {
                out = input.insetVal
                return@measureCode
            }

            val output = input.connected.singleOrNull() ?: return@measureCode
            if (store.containsKey(output)) {
                out = store[output]!!.get() as T?
                return@measureCode
            }
            out = output.defaultHandler(this) as T?
        }
        return out
    }

    fun emit(output: NodeContext.BoundOutput<Unit>, now: Boolean = false) {
        tasks.add {
            output.connected.singleOrNull()?.signalListener?.invoke(this)
        }
        if (now) taskLoop()
    }

    private fun taskLoop() {
        while (tasks.isNotEmpty()) {
            global.measureCode {
                tasks.pop().run()
            }
            if (global.cpuLimit()) break
        }
    }

    fun child(shareLocals: Boolean): EvaluationContext {
        val c = if (shareLocals) EvaluationContext(global, varStore) else EvaluationContext(global)
        if (functionStack.isNotEmpty()) c.functionStack.push(functionStack.peek())
        for ((k, v) in store) c.store[k] = v
        return c
    }
}