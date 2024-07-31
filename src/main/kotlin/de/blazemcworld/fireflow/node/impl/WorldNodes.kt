package de.blazemcworld.fireflow.node.impl

import de.blazemcworld.fireflow.node.*
import net.minestom.server.instance.batch.AbsoluteBlockBatch
import net.minestom.server.instance.block.Block
import net.minestom.server.item.Material
import kotlin.math.max
import kotlin.math.min

object SetBlockNode : BaseNode("Set Block", Material.DISPENSER) {
    private val signal = input("Signal", SignalType)
    private val pos = input("Position", PositionType)
    private val block = input("Block", TextType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = {
            it[ctx[pos]]?.let { pos ->
                val type = Block.fromNamespaceId(it[ctx[block]] ?: return@let) ?: return@let
                ctx.global.space.playInstance.setBlock(pos, type)
            }
            it.emit(ctx[next])
        }
    }
}

object FillBlocksNode : BaseNode("Fill Blocks", Material.DISPENSER) {
    private val signal = input("Signal", SignalType)
    private val start = input("Start", PositionType)
    private val end = input("End", PositionType)
    private val block = input("Block", TextType)
    private val next = output("Next", SignalType)

    override fun setup(ctx: NodeContext) {
        ctx[signal].signalListener = {
            it[ctx[start]]?.let { start ->
                val type = Block.fromNamespaceId(it[ctx[block]] ?: return@let) ?: return@let
                val end = it[ctx[end]] ?: return@let

                val minX = min(start.x, end.x).toInt()
                val minY = min(start.y, end.y).toInt()
                val minZ = min(start.z, end.z).toInt()

                val maxX = max(start.x, end.x).toInt()
                val maxY = max(start.y, end.y).toInt()
                val maxZ = max(start.z, end.z).toInt()

                if ((maxX - minX) * (maxY - minY) * (maxZ - minZ) > 1000000) return@let

                val batch = AbsoluteBlockBatch()
                for (x in minX..maxX) {
                    for (y in minY..maxY) {
                        for (z in minZ..maxZ) {
                            batch.setBlock(x, y, z, type)
                        }
                    }
                }
                batch.apply(ctx.global.space.playInstance, null)
            }
            it.emit(ctx[next])
        }
    }
}

object GetBlockNode : BaseNode("Get Block", Material.OBSERVER) {
    private val pos = input("Position", PositionType)
    private val block = output("Block", TextType)

    override fun setup(ctx: NodeContext) {
        ctx[block].defaultHandler = {
            it[ctx[pos]]?.let { pos -> ctx.global.space.playInstance.getBlock(pos).namespace().path() }
        }
    }
}