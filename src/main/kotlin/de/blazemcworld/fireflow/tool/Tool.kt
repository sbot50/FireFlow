package de.blazemcworld.fireflow.tool

import de.blazemcworld.fireflow.space.Space
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.Player
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

interface Tool {

    val item: ItemStack
    fun handler(player: Player, space: Space): Handler

    companion object {
        val allTools = setOf(
            MoveNodeTool,
            CreateNodeTool,
            DeleteNodeTool,
            ConnectNodesTool
        )
    }

   interface Handler {
       val tool: Tool
       fun select() {}
       fun deselect() {}
       fun use() {}
   }

    fun item(material: Material, name: String, color: TextColor, vararg description: String) = ItemStack.builder(material)
        .customName(Component.text(name).color(color).decoration(TextDecoration.ITALIC, false))
        .lore(description.map {
            Component.text(it).color(NamedTextColor.GRAY).decoration(TextDecoration.ITALIC, false)
        }).build()
}