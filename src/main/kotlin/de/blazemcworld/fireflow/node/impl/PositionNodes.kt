package de.blazemcworld.fireflow.node.impl

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import de.blazemcworld.fireflow.node.PositionType
import net.minestom.server.coordinate.Pos
import net.minestom.server.item.Material

object UnpackPositionNode : BaseNode("Unpack Position", Material.CHEST) {
    private val position = input("Position", PositionType)
    private val x = output("X", NumberType)
    private val y = output("Y", NumberType)
    private val z = output("Z", NumberType)
    private val yaw = output("Yaw", NumberType)
    private val pitch = output("Pitch", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[x].defaultHandler = { it[ctx[position]]?.x }
        ctx[y].defaultHandler = { it[ctx[position]]?.y }
        ctx[z].defaultHandler = { it[ctx[position]]?.z }
        ctx[yaw].defaultHandler = { it[ctx[position]]?.yaw?.toDouble() }
        ctx[pitch].defaultHandler = { it[ctx[position]]?.pitch?.toDouble() }
    }

}

object PackPositionNode : BaseNode("Pack Position", Material.ENDER_CHEST) {
    private val position = input("Default", PositionType, true)
    private val x = input("X", NumberType, true)
    private val y = input("Y", NumberType, true)
    private val z = input("Z", NumberType, true)
    private val yaw = input("Yaw", NumberType, true)
    private val pitch = input("Pitch", NumberType, true)
    private val newPos = output("New", PositionType)

    override fun setup(ctx: NodeContext) {
        ctx[newPos].defaultHandler = { eval ->
            var pos = eval[ctx[position]] ?: Pos.ZERO

            eval[ctx[x]]?.let { pos = pos.withX(it) }
            eval[ctx[y]]?.let { pos = pos.withY(it) }
            eval[ctx[z]]?.let { pos = pos.withZ(it) }
            eval[ctx[yaw]]?.let { pos = pos.withYaw(it.toFloat()) }
            eval[ctx[pitch]]?.let { pos = pos.withPitch(it.toFloat()) }

            pos
        }
    }

}