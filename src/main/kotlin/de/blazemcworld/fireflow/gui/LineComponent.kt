package de.blazemcworld.fireflow.gui

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.coordinate.Vec
import net.minestom.server.entity.Entity
import net.minestom.server.entity.EntityType
import net.minestom.server.entity.metadata.display.TextDisplayMeta
import net.minestom.server.instance.Instance
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

class LineComponent {

    private val display = Entity(EntityType.TEXT_DISPLAY).apply {
        setNoGravity(true)
        val meta = entityMeta as TextDisplayMeta
        meta.text = Component.text("-")
        meta.backgroundColor = 0
    }

    var start = Pos2d.ZERO
    var end = Pos2d.ZERO
    var color = NamedTextColor.WHITE

    fun update(inst: Instance) {
        val meta = display.entityMeta as TextDisplayMeta
        meta.text = Component.text("-").color(color)

        val dist = start.distance(end)
        meta.scale = Vec(dist * 8, 1.0, 1.0)
        val angle = atan2(end.y - start.y, start.x - end.x).toFloat()
        meta.leftRotation = listOf(0f, 0f, sin(angle / 2), cos(angle / 2)).toFloatArray()
        val v = ((start + end) * 0.5) + Pos2d(
            cos(angle) * dist * 0.1 - sin(angle) * 0.1625,
            -sin(angle) * dist * 0.1 - cos(angle) * 0.1625
        )
        display.setInstance(inst, v.to3d(15.999).withView(180f, 0f))
    }

    fun remove() {
        display.remove()
    }
}