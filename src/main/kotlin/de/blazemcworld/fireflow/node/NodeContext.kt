package de.blazemcworld.fireflow.node

import de.blazemcworld.fireflow.gui.IOComponent
import de.blazemcworld.fireflow.gui.NodeComponent

class NodeContext(val global: GlobalNodeContext, val component: NodeComponent) {

    private val store = HashMap<BaseNode.IO<*>, Bound<*>>()

    operator fun <T> get(v: BaseNode.Output<T>) = store[v] as BoundOutput<T>
    operator fun <T> get(v: BaseNode.Input<T>) = store[v] as BoundInput<T>

    init {
        for (i in component.inputs) {
            store[i.io] = if (i.io.type.insetable && i is IOComponent.InsetInput<*> && i.insetVal != null)
                BoundInsetInput(i) else BoundInput(i.io)
        }
        for (o in component.outputs) store[o.io] = BoundOutput(o.io)
    }

    fun computeConnections() {
        for (v in store.values) v.computeConnections()
    }

    abstract class Bound<T : BaseNode.IO<*>>(val v: T) {
        abstract fun computeConnections()
    }

    inner class BoundOutput<T>(v: BaseNode.Output<T>) : Bound<BaseNode.Output<T>>(v) {
        lateinit var connected: Set<BoundInput<*>>
        var defaultHandler: (EvaluationContext) -> T? = { null }

        override fun computeConnections() {
            connected = component.outputs.find { it.io == v }?.connections?.map { global.nodeContexts[it.node]!![it.io] }?.toSet() ?: emptySet()
        }
    }

    open inner class BoundInput<T>(v: BaseNode.Input<T>) : Bound<BaseNode.Input<T>>(v) {
        var signalListener: (EvaluationContext) -> Unit = {}

        open lateinit var connected: Set<BoundOutput<*>>
        override fun computeConnections() {
            connected = component.inputs.find { it.io == v }?.connections?.map { global.nodeContexts[it.output.node]!![it.output.io] }?.toSet() ?: emptySet()
        }
    }

    inner class BoundInsetInput<T>(insetValInp: IOComponent.InsetInput<T>) : BoundInput<T>(insetValInp.io as BaseNode.Input<T>) {
        var insetVal: T? = insetValInp.insetVal
        override var connected = emptySet<BoundOutput<*>>()
    }

}