package de.blazemcworld.fireflow.node

import de.blazemcworld.fireflow.node.impl.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import net.minestom.server.inventory.InventoryType
import net.minestom.server.item.ItemStack
import net.minestom.server.item.Material

class NodeCategory(val title: String, color: TextColor, material: Material, val invType: InventoryType, init: NodeCategory.() -> Unit) {
    val item: ItemStack
    val categories = mutableMapOf<Int, NodeCategory>()
    val nodes = mutableMapOf<Int, Node>()
    var userFunctions = false

    init {
        init(this)
        item = ItemStack.builder(material).customName(Component.text(title).color(color).decoration(TextDecoration.ITALIC, false)).hideExtraTooltip().build()
    }

    /*
    * Chest Layout Help:
    *  0   1   2    3   4   5    6   7   8
    *  9  10  11   12  13  14   15  16  17
    * 18  19  20   21  22  23   24  25  26
    *
    * 27  28  29   30  31  32   33  34  35
    * 36  37  38   39  40  41   42  43  44
    * 45  46  47   48  49  50   51  52  53
    */

    companion object {
        val root = NodeCategory("Select a Node", NamedTextColor.GRAY, Material.BOOK, InventoryType.CHEST_5_ROW) {
            slot(1, "Events", NamedTextColor.AQUA, Material.LIGHT_BLUE_DYE, InventoryType.CHEST_3_ROW) {
                slot(0, OnPlayerJoinNode)
                slot(1, OnPlayerChatNode)
            }
            slot(3, "Numbers", NamedTextColor.RED, Material.SLIME_BALL, InventoryType.CHEST_3_ROW) {
                slot(0, AddNumbersNode)
                slot(1, SubtractNumbersNode)
                slot(2, MultiplyNumbersNode)
                slot(3, DivideNumbersNode)
                slot(4, ModuloNumbersNode)
                slot(5, PowerNumbersNode)
                slot(6, RandomNumberNode)
            }
            slot(5, "Players", NamedTextColor.GOLD, Material.PLAYER_HEAD, InventoryType.CHEST_3_ROW) {
                slot(0, SendMessageNode)
                slot(1, PlayerPositionNode)
                slot(2, KillPlayerNode)
            }
            slot(7, "Flow", NamedTextColor.BLUE, Material.WATER_BUCKET, InventoryType.CHEST_3_ROW) {
                slot(0, ScheduleNode)
            }
            slot(18, "Value Literals", NamedTextColor.GRAY, Material.WRITABLE_BOOK, InventoryType.CHEST_3_ROW) {
                for ((i, v) in ValueLiteralNode.all.withIndex()) slot(i, v)
            }
            slot(20, "User Functions", TextColor.color(66, 245, 182), Material.DIAMOND, InventoryType.CHEST_3_ROW) {
                userFunctions = true
            }
            slot(22, "Text", TextType.color, Material.BOLT_ARMOR_TRIM_SMITHING_TEMPLATE, InventoryType.CHEST_3_ROW) {
                slot(0, ToTextNode)
                slot(1, ConcatNode)
                slot(2, SubtextNode)
            }
            slot(24, "Messages", MessageType.color, Material.FLOW_ARMOR_TRIM_SMITHING_TEMPLATE, InventoryType.CHEST_3_ROW) {
                slot(0, ToMessageNode)
                slot(1, FormatMiniMessageNode)
            }
            slot(26, "Dictionaries", DictionaryType.color, Material.COBWEB, InventoryType.CHEST_3_ROW) {
                slot(0, EmptyDictionaryNode)
                slot(1, DictionaryGetNode)
                slot(2, DictionarySetNode)
                slot(3, DictionarySizeNode)
                slot(4, DictionaryRemoveNode)
                slot(5, DictionaryKeysNode)
            }
            slot(37, "Positions", NamedTextColor.YELLOW, Material.COMPASS, InventoryType.CHEST_3_ROW) {
                slot(0, PackPositionNode)
                slot(1, UnpackPositionNode)
            }
            slot(39, "World", NamedTextColor.GREEN, Material.GRASS_BLOCK, InventoryType.CHEST_3_ROW) {
                slot(0, GetBlockNode)
                slot(1, SetBlockNode)
                slot(2, FillBlocksNode)
            }
            slot(41, "Variables", NamedTextColor.LIGHT_PURPLE, Material.ANVIL, InventoryType.CHEST_4_ROW) {
                slot(10, VariableNodes.getLocal)
                slot(13, VariableNodes.getSpace)
                slot(16, VariableNodes.getPersistent)
                slot(19, VariableNodes.setLocal)
                slot(22, VariableNodes.setSpace)
                slot(25, VariableNodes.setPersistent)
            }
            slot(43, "Lists", NamedTextColor.WHITE, Material.SHULKER_BOX, InventoryType.CHEST_3_ROW) {
                slot(0, EmptyListNode)
                slot(1, ListAppendNode)
                slot(2, ListGetNode)
                slot(3, ListLengthNode)
                slot(4, ListInsertNode)
                slot(5, ListRemoveNode)
            }
        }
    }

    private fun slot(id: Int, name: String, color: TextColor, material: Material, invType: InventoryType, handler: NodeCategory.() -> Unit) {
        categories[id] = NodeCategory(name, color, material, invType, handler)
    }

    private fun slot(id: Int, child: Node) {
        nodes[id] = child
    }
}