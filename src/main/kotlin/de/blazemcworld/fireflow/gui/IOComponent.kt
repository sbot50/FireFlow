package de.blazemcworld.fireflow.gui

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.SignalType
import de.blazemcworld.fireflow.node.ValueType
import de.blazemcworld.fireflow.space.Space
import net.kyori.adventure.text.Component
import net.minestom.server.instance.Instance
import kotlin.math.max
import kotlin.math.min

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

    open class Input(val io: BaseNode.Input<*>, node: NodeComponent) : IOComponent(node) {
        val connections = mutableSetOf<Output>()

        init {
            updateText()
        }

        private fun updateText() {
            if (this is InsetInput<*> && insetVal != null) {
                val display = stringify()

                text.text = Component.text("⏹ " + display.substring(0..max(0,min(display.length-1, 10))) + (if (display.length > 10) "..." else "") ).color(io.type.color)
            } else {
                text.text = Component.text("○ " + io.name).color(io.type.color)
            }
        }

        fun connect(output: Output): Boolean {
            if (output.io.type != io.type) return false

            if (io.type is SignalType) {
                output.disconnectAll()
            } else {
                disconnectAll()
            }
            connections.add(output)
            output.connections.add(this)
            return true
        }

        override fun disconnectAll() {
            for (output in connections) {
                output.connections.remove(this)
            }
            connections.clear()
            for (line in lines) {
                line.remove()
            }
            lines.clear()
            lineOutputMap.clear()
        }

        val lines = mutableListOf<LineComponent>()
        val lineOutputMap = mutableMapOf<LineComponent, Output>()
        override fun update(inst: Instance) {
            updateText()

            val origin = Pos2d(pos.x + text.width(), pos.y + text.height() * 0.75)

            while (lines.size > connections.size) lines.removeLast().remove()
            while (lines.size < connections.size) lines.add(LineComponent())

            lineOutputMap.clear()
            for ((line, output) in lines.zip(connections)) {
                line.start = origin
                line.end = Pos2d(output.pos.x, output.pos.y + output.text.height() * 0.75)
                line.color = io.type.color
                line.update(inst)

                lineOutputMap[line] = output
            }
            super.update(inst)
        }
    }

    class InsetInput<T>(val input : BaseNode.Input<T>, node: NodeComponent, var insetVal: T? = input.default, val type: ValueType<T> = input.type) : Input(input, node) {
        fun stringify(): String {
            return type.stringify(insetVal ?: return "unset")
        }

        fun updateInset(string: String, space: Space) {
            insetVal = type.parse(string, space)
        }

        fun searlize(): JsonElement {
            return type.serialize(insetVal ?: return JsonObject(), mutableMapOf())
        }

        fun deserialize(json: JsonElement, space: Space) {
            insetVal = type.deserialize(json, space, mutableMapOf())
        }
    }

    class Output(val io: BaseNode.Output<*>, node: NodeComponent) : IOComponent(node) {
        val connections = mutableSetOf<Input>()
        init {
            text.text = Component.text(io.name + " ○").color(io.type.color)
        }

        fun connect(input: Input) = input.connect(this)

        override fun disconnectAll() {
            for (input in connections) {
                input.connections.remove(this)
                val removeMe = mutableSetOf<LineComponent>()
                for ((line, output) in input.lineOutputMap) {
                    if (output == this) {
                        removeMe.add(line)
                        input.lines.remove(line)
                        line.remove()
                    }
                }
                removeMe.forEach(input.lineOutputMap::remove)
            }
            connections.clear()
        }

        override fun update(inst: Instance) {
            for (input in connections) input.update(inst)
            super.update(inst)
        }
    }

}