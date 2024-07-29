package de.blazemcworld.fireflow.tool

import de.blazemcworld.fireflow.space.Space
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player
import net.minestom.server.item.Material

object DeleteNodeTool : Tool {
    override val item = item(Material.REDSTONE_BLOCK,
        "Delete", NamedTextColor.RED,
        "Used for deleting nodes",
        "or connections in the code"
    )

    override fun handler(player: Player, space: Space) = object : Tool.Handler {
        override val tool = DeleteNodeTool

        override fun use() {
            val cursor = space.codeCursor(player)
            space.codeNodes.find { it.includes(cursor) }?.let {
                it.remove()
                space.codeNodes.remove(it)
                return
            }

            for (node in space.codeNodes) {
                for (input in node.inputs) {
                    for (line in input.lines) {
                        if (line.distance(cursor) < 0.1) {
                            input.connections.remove(input.lineOutputMap[line])
                            input.update(space.codeInstance)
                            return
                        }
                    }
                }
            }
        }
    }
}