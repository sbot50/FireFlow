package de.blazemcworld.fireflow.node.impl

import de.blazemcworld.fireflow.node.BaseNode
import de.blazemcworld.fireflow.node.NodeContext
import de.blazemcworld.fireflow.node.NumberType
import de.blazemcworld.fireflow.node.VectorType
import net.minestom.server.coordinate.Vec
import net.minestom.server.item.Material

object UnpackVectorNode : BaseNode("Unpack Vector", Material.CHEST) {
    private val vector = input("Vector", VectorType)
    private val x = output("X", NumberType)
    private val y = output("Y", NumberType)
    private val z = output("Z", NumberType)

    override fun setup(ctx: NodeContext) {
        ctx[x].defaultHandler = { it[ctx[vector]]?.x }
        ctx[y].defaultHandler = { it[ctx[vector]]?.y }
        ctx[z].defaultHandler = { it[ctx[vector]]?.z }
    }
}

object PackVectorNode : BaseNode("Pack Vector", Material.ENDER_CHEST) {
    private val vector = input("Default", VectorType, optional=true)
    private val x = input("X", NumberType, optional=true)
    private val y = input("Y", NumberType, optional=true)
    private val z = input("Z", NumberType, optional=true)
    private val newPos = output("New", VectorType)

    override fun setup(ctx: NodeContext) {
        ctx[newPos].defaultHandler = { eval ->
            var vec = eval[ctx[vector]] ?: Vec.ZERO

            eval[ctx[x]]?.let { vec = vec.withX(it) }
            eval[ctx[y]]?.let { vec = vec.withY(it) }
            eval[ctx[z]]?.let { vec = vec.withZ(it) }

            vec
        }
    }

}