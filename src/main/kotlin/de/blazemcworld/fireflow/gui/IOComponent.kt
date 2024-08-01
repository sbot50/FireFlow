package de.blazemcworld.fireflow.gui

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.SignalType
import net.kyori.adventure.text.Component
import net.minestom.server.instance.Instance

abstract class IOComponent(val node: NodeComponent) {

    val text = TextComponent()
    var pos = Pos2d.ZERO

    open fun update(inst: Instance) {
        text.pos = pos
        text.update(inst)
    }

    fun remove() {
        text.remove()
        disconnectAll()
    }

    fun includes(cursor: Pos2d) = text.includes(cursor)

    abstract fun disconnectAll()

    class Input(val io: BaseNode.Input<*>, node: NodeComponent) : IOComponent(node) {
        val connections = mutableSetOf<ConnectionComponent>()

        init {
            text.text = Component.text("○ " + io.name).color(io.type.color)
        }

        fun connect(output: Output, relays: List<Pos2d>): Boolean {
            if (output.io.type != io.type) return false

            if (io.type is SignalType) {
                output.disconnectAll()
            } else {
                disconnectAll()
            }
            connections.add(ConnectionComponent(this, output).also { it.relays += relays })
            output.connections.add(this)
            return true
        }

        override fun disconnectAll() {
            for (connection in connections) {
                connection.output.connections.remove(this)
            }
            for (line in connections) {
                line.remove()
            }
            connections.clear()
        }

        override fun update(inst: Instance) {
            for (connection in connections) {
                connection.update(inst)
            }
            super.update(inst)
        }
    }
    class Output(val io: BaseNode.Output<*>, node: NodeComponent) : IOComponent(node) {
        val connections = mutableSetOf<Input>()
        init {
            text.text = Component.text(io.name + " ○").color(io.type.color)
        }

        fun connect(input: Input, relays: List<Pos2d>) = input.connect(this, relays)

        override fun disconnectAll() {
            for (input in connections) {
                input.connections.removeIf {
                    if (it.output != this) return@removeIf false
                    it.remove()
                    true
                }
            }
            connections.clear()
        }

        override fun update(inst: Instance) {
            for (input in connections) input.update(inst)
            super.update(inst)
        }
    }

}