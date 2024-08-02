package de.blazemcworld.fireflow.gui

import net.minestom.server.instance.Instance
import kotlin.math.min

class ConnectionComponent(val input: IOComponent.Input, val output: IOComponent.Output) {
    val relays = mutableListOf<Pos2d>()
    val relayLines = mutableListOf<LineComponent>()
    val finalLine = LineComponent().also { it.color = input.io.type.color }

    fun update(inst: Instance) {
        var current = Pos2d(output.pos.x, output.pos.y + output.text.height() * 0.75)
        while (relayLines.size < relays.size) relayLines.add(LineComponent().apply { color = input.io.type.color })
        while (relayLines.size > relays.size) relayLines.removeLast().also { it.remove() }

        for ((index, pos) in relays.withIndex()) {
            relayLines[index].start = current
            current = pos
            relayLines[index].end = current
            relayLines[index].update(inst)
        }

        finalLine.start = current
        current = Pos2d(input.pos.x + input.text.width(), input.pos.y + input.text.height() * 0.75)
        finalLine.end = current
        finalLine.update(inst)
    }

    fun remove() {
        for (line in relayLines) line.remove()
        finalLine.remove()
    }

    fun distance(cursor: Pos2d) = min(relayLines.minOfOrNull{ it.distance(cursor) } ?: Double.MAX_VALUE, finalLine.distance(cursor))
}