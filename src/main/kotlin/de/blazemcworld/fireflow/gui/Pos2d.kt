package de.blazemcworld.fireflow.gui

import net.minestom.server.coordinate.Pos
import kotlin.math.pow
import kotlin.math.sqrt

data class Pos2d(val x: Double, val y: Double) {
    constructor(x: Number, y: Number) : this(x.toDouble(), y.toDouble())

    companion object {
        val ZERO = Pos2d(0, 0)
    }

    fun distance(other: Pos2d) = sqrt((x - other.x).pow(2) + (y - other.y).pow(2))

    operator fun plus(other: Pos2d) = Pos2d(x + other.x, y + other.y)
    operator fun minus(other: Pos2d) = Pos2d(x - other.x, y - other.y)
    operator fun times(other: Pos2d) = Pos2d(x * other.x, y * other.y)
    operator fun div(other: Pos2d) = Pos2d(x / other.x, y / other.y)
    operator fun times(scale: Double) = Pos2d(x * scale, y * scale)

    fun to3d(z: Double): Pos = Pos(x, y, z)
}
