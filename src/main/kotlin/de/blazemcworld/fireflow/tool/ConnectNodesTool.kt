package de.blazemcworld.fireflow.tool

import de.blazemcworld.fireflow.gui.IOComponent
import de.blazemcworld.fireflow.gui.LineComponent
import de.blazemcworld.fireflow.gui.Pos2d
import de.blazemcworld.fireflow.space.Space
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.item.Material
import net.minestom.server.timer.Task
import net.minestom.server.timer.TaskSchedule

object ConnectNodesTool : Tool {
    override val item = item(Material.BREEZE_ROD,
        "Connect Nodes", NamedTextColor.AQUA,
        "Used for connecting node",
        "inputs and outputs."
    )

    override fun handler(player: Player, space: Space) = object : Tool.Handler {
        override val tool = ConnectNodesTool

        private var from: IOComponent.Output? = null
        private var previewLine = LineComponent()
        private var previewTask: Task? = null

        private var highlighter: Tool.IOHighlighter? = Tool.IOHighlighter(NamedTextColor.AQUA, player, space)

        override fun use() {
            val cursor = space.codeCursor(player)
            for (node in space.codeNodes) {
                for (output in node.outputs) {
                    if (output.includes(cursor)) {
                        if (from == output) {
                            clearSelectionPreview()
                            return
                        }
                        clearSelectionPreview()
                        from = output
                        previewLine = LineComponent()
                        previewLine.color = output.io.type.color

                        previewTask?.cancel()
                        previewTask = MinecraftServer.getSchedulerManager().submitTask {
                            previewLine.start = Pos2d(output.pos.x, output.pos.y + output.text.height() * 0.75)
                            previewLine.end = space.codeCursor(player)
                            previewLine.update(space.codeInstance)
                            return@submitTask TaskSchedule.tick(1)
                        }
                        return
                    }
                }
                from?.let { output ->
                    for (input in node.inputs) {
                        if (input.includes(cursor)) {
                            if (!input.connect(output)) return

                            if (input is IOComponent.InsetInput<*> && input.insetVal != null) {
                                input.insetVal = null
                            }

                            input.node.update(space.codeInstance)
                            clearSelectionPreview()
                            return
                        }
                    }
                }
            }
        }

        override fun select() {
            highlighter?.selected()
        }

        fun clearSelectionPreview() {
            from = null
            previewLine.remove()
            previewTask?.cancel()
            previewTask = null
        }

        override fun deselect() {
            clearSelectionPreview()
            highlighter?.deselect()
        }
    }
}