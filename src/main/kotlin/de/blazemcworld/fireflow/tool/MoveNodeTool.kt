package de.blazemcworld.fireflow.tool

import de.blazemcworld.fireflow.gui.NodeComponent
import de.blazemcworld.fireflow.gui.Pos2d
import de.blazemcworld.fireflow.gui.RectangleComponent
import de.blazemcworld.fireflow.space.Space
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule
import kotlin.math.max
import kotlin.math.min

object MoveNodeTool : Tool {
    override val item = item(Material.PISTON,
        "Move Node", NamedTextColor.YELLOW,
        "Used for moving nodes",
        "around in the code"
    )

    override fun handler(player: Player, space: Space) = object : Tool.Handler {
        override val tool = MoveNodeTool

        val nodes = mutableMapOf<NodeComponent, Pos2d>()
        var selectionStart: Pos2d? = null
        var moveTask: Task? = null
        var selectionIndicator = RectangleComponent()
        var selectionTask: Task? = null

        override fun use() {
            if (nodes.isNotEmpty()) {
                stopMoving()
                return
            }
            val cursor = space.codeCursor(player)
            selectionStart?.let { start ->
                selectionStart = null
                selectionTask?.cancel()
                selectionIndicator.remove()

                val min = Pos2d(min(start.x, cursor.x), min(start.y, cursor.y))
                val max = Pos2d(max(start.x, cursor.x), max(start.y, cursor.y))

                space.codeNodes.forEach {
                    if (it.outline.pos.x < min.x || it.outline.pos.x + it.outline.size.x > max.x) return@forEach
                    if (it.outline.pos.y < min.y || it.outline.pos.y + it.outline.size.y > max.y) return@forEach

                    nodes[it] = it.pos - cursor
                    it.outline.setColor(NamedTextColor.GREEN)
                    it.isBeingMoved = true
                    it.update(space.codeInstance)

                    moveTask = MinecraftServer.getSchedulerManager().submitTask task@{
                        val movedCursor = space.codeCursor(player)
                        nodes.forEach { (n, offset) ->
                            n.pos = movedCursor + offset
                            n.update(space.codeInstance)
                        }
                        if (nodes.isEmpty()) return@task TaskSchedule.stop()
                        return@task TaskSchedule.tick(1)
                    }
                }
                return@let
            }
            space.codeNodes.find { it.includes(cursor) }?.let {
                nodes[it] = it.pos - cursor
                it.outline.setColor(NamedTextColor.GREEN)
                it.isBeingMoved = true
                it.update(space.codeInstance)

                moveTask = MinecraftServer.getSchedulerManager().submitTask task@{
                    val movedCursor = space.codeCursor(player)
                    nodes.forEach { (n, offset) ->
                        n.pos = movedCursor + offset
                        n.update(space.codeInstance)
                    }
                    if (nodes.isEmpty()) return@task TaskSchedule.stop()
                    return@task TaskSchedule.tick(1)
                }
            }

            if (nodes.isEmpty()) {
                selectionStart = cursor
                selectionTask?.cancel()
                selectionIndicator.remove()
                selectionIndicator = RectangleComponent()
                selectionIndicator.setColor(NamedTextColor.AQUA)
                selectionIndicator.pos = cursor
                selectionTask = MinecraftServer.getSchedulerManager().submitTask task@{
                    selectionIndicator.size = space.codeCursor(player) - selectionIndicator.pos
                    selectionIndicator.update(space.codeInstance)
                    return@task TaskSchedule.tick(1)
                }
            }
        }

        private fun stopMoving() {
            nodes.forEach { (node, _) ->
                node.outline.setColor(NamedTextColor.WHITE)
                node.isBeingMoved = false
                node.update(space.codeInstance)
            }
            nodes.clear()
            moveTask?.cancel()
            moveTask = null
            selectionTask?.cancel()
            selectionIndicator.remove()
        }

        override fun deselect() {
            stopMoving()
        }
    }
}