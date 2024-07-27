package de.blazemcworld.fireflow.gui

import net.minestom.server.instance.Instance

class RectangleComponent {

    private val top = LineComponent()
    private val bottom = LineComponent()
    private val left = LineComponent()
    private val right = LineComponent()

    var pos = Pos2d.ZERO
    var size = Pos2d.ZERO

    fun update(inst: Instance) {
        top.start = pos
        top.end = Pos2d(pos.x + size.x, pos.y)
        right.start = top.end
        right.end = pos + size
        bottom.start = right.end
        bottom.end = Pos2d(pos.x, pos.y + size.y)
        left.start = bottom.end
        left.end = pos

        top.update(inst)
        bottom.update(inst)
        left.update(inst)
        right.update(inst)
    }

    fun remove() {
        top.remove()
        bottom.remove()
        left.remove()
        right.remove()
    }

    fun includes(check: Pos2d): Boolean {
        if (size.x > 0 && (pos.x > check.x || pos.x + size.x < check.x)) return false
        if (size.y > 0 && (pos.y > check.y || pos.y + size.y < check.y)) return false

        if (size.x < 0 && (pos.x + size.x > check.x || pos.x < check.x)) return false
        if (size.y < 0 && (pos.y + size.y > check.y || pos.y < check.y)) return false

        return true
    }
}