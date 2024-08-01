package de.blazemcworld.fireflow.inventory

import de.blazemcworld.fireflow.node.*
import de.blazemcworld.fireflow.node.impl.NodeList
import de.blazemcworld.fireflow.space.Space
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.entity.Player
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack

object SelectionInventories {

    fun selectNode(player: Player, space: Space, category: NodeCategory, callback: (BaseNode) -> Unit) {
        val inv = Inventory(category.invType, category.title)

        for ((slot, node) in category.nodes) {
            inv.setItemStack(slot, node.menuItem())
        }
        for ((slot, sub) in category.categories) {
            inv.setItemStack(slot, sub.item)
        }

        val functions = mutableMapOf<Int, FunctionCallNode>()
        if (category.userFunctions) {
            for ((i, f) in space.functionNodes.withIndex()) {
                if (i >= inv.size) break
                functions[i] = f
                inv.setItemStack(i, f.menuItem())
            }
        }

        inv.addInventoryCondition click@{ who, slot, _, _ ->
            if (who != player) return@click
            if (category.categories.containsKey(slot)) {
                player.closeInventory()
                selectNode(player, space, category.categories[slot]!!, callback)
                return@click
            }

            if (!category.nodes.containsKey(slot) && !functions.containsKey(slot)) return@click
            player.closeInventory()
            val node = if (functions.containsKey(slot)) functions[slot] else category.nodes[slot]
            if (node in NodeList.all || node in space.functionNodes) {
                when (node) {
                    is BaseNode -> callback(node)
                    is GenericNode -> {
                        val todo = node.generics.keys.toMutableList()
                        if (todo.isEmpty()) {
                            throw IllegalStateException("$node without generics should not be a GenericNode!")
                        }

                        val generics = mutableMapOf<String, ValueType<*>>()
                        fun selectNext() {
                            val id = todo.removeFirst()
                            selectValueType(player, id, node.generics[id]!!) {
                                generics[id] = it
                                if (todo.isEmpty()) {
                                    callback(node.create(generics))
                                } else selectNext()
                            }
                        }
                        selectNext()
                    }
                }
            }
        }

        player.openInventory(inv)
    }

    fun selectValueType(player: Player, name: String, types: List<SomeType>, callback: (ValueType<*>) -> Unit) {
        val inv = Inventory(InventoryType.CHEST_6_ROW, "Select $name")

        for ((slot, type) in types.withIndex()) {
            inv.setItemStack(slot, ItemStack.builder(type.material)
                .customName(Component.text(type.name).color(type.color).decoration(TextDecoration.ITALIC, false)).build())
        }

        inv.addInventoryCondition click@{ who, slot, _, _ ->
            if (who != player) return@click
            val type = types.getOrNull(slot) ?: return@click
            player.closeInventory()

            when (type) {
                is ValueType<*> -> callback(type)
                is GenericType -> {
                    val todo = type.generics.keys.toMutableList()
                    if (todo.isEmpty()) {
                        throw IllegalStateException("$type without generics should not be a GenericType!")
                    }

                    val generics = mutableMapOf<String, ValueType<*>>()
                    fun selectNext() {
                        val id = todo.removeFirst()
                        selectValueType(player, id, type.generics[id]!!) {
                            generics[id] = it
                            if (todo.isEmpty()) {
                                callback(type.create(generics))
                            } else selectNext()
                        }
                    }
                    selectNext()
                }
            }
        }

        player.openInventory(inv)
    }
}