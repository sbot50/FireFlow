package de.blazemcworld.fireflow.tool

import de.blazemcworld.fireflow.gui.NodeComponent
import de.blazemcworld.fireflow.gui.Pos2d
import de.blazemcworld.fireflow.space.Space
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule

object MoveNodeTool : Tool {
    override val item = item(Material.PISTON,
        "Move Node", NamedTextColor.YELLOW,
        "Used for moving nodes",
        "around in the code"
    )

    override fun handler(player: Player, space: Space) = object : Tool.Handler {
        override val tool = MoveNodeTool

        var node: NodeComponent? = null
        var offset = Pos2d.ZERO
        var moveTask: Task? = null

        override fun use() {
            if (node != null) {
                stopMoving()
                return
            }
            val cursor = space.codeCursor(player)
            space.codeNodes.find { it.includes(cursor) }?.let {
                node = it
                offset = it.pos - cursor
                it.setOutlineColor(NamedTextColor.GREEN)
                it.update(space.codeInstance)

                moveTask = MinecraftServer.getSchedulerManager().submitTask task@{
                    val movedCursor = space.codeCursor(player)
                    node?.let { n ->
                        n.pos = movedCursor + offset
                        n.update(space.codeInstance)
                    }
                    if (node == null) return@task TaskSchedule.stop()
                    return@task TaskSchedule.tick(1)
                }
            }
        }

        private fun stopMoving() {
            node?.let {
                it.setOutlineColor(NamedTextColor.WHITE)
                it.update(space.codeInstance)
                node = null
            }
            moveTask?.cancel()
            moveTask = null
        }

        override fun deselect() {
            if (node != null) stopMoving()
        }
    }
}