package de.blazemcworld.fireflow.gui

import de.blazemcworld.fireflow.node.NodeIO
import net.kyori.adventure.text.Component
import net.minestom.server.instance.Instance

abstract class IOComponent {

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

    class Input(val io: NodeIO) : IOComponent() {
        val connections = mutableSetOf<Output>()

        init {
            text.text = Component.text("○ " + io.name).color(io.type.color)
        }

        fun connect(output: Output): Boolean {
            if (output.io.type != io.type) return false

            when (io.type.flow) {
                NodeIO.Flow.FORWARDS -> output.disconnectAll()
                NodeIO.Flow.BACKWARDS -> disconnectAll()
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
    class Output(val io: NodeIO) : IOComponent() {
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