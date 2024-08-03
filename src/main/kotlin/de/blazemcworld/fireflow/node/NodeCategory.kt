package de.blazemcworld.fireflow.node

import de.blazemcworld.fireflow.node.impl.*
import de.blazemcworld.fireflow.node.impl.player.action.*
import de.blazemcworld.fireflow.node.impl.player.event.*
import de.blazemcworld.fireflow.node.impl.player.info.*
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
            slot(0, "Events", NamedTextColor.AQUA, Material.LIGHT_BLUE_DYE, InventoryType.CHEST_3_ROW) {
                slot(0, OnPlayerJoinNode)
                slot(1, OnPlayerChatNode)
                slot(2, OnPlayerDeathNode)
                slot(3, OnPlayerBlockInteraction)
                slot(4, OnPlayerPlaceBlockNode)
                slot(5, OnPlayerBreakBlockNode)
                slot(6, OnPlayerStartDiggingNode)
                slot(7, OnPlayerCancelDiggingNode)
                slot(8, OnPlayerFinishDiggingNode)
                slot(9, OnPlayerStartElytraFlyingNode)
                slot(10, OnPlayerStopElytraFlyingNode)
                slot(11, OnPlayerStartFlyingNode)
                slot(12, OnPlayerStopFlyingNode)
                slot(13, OnPlayerStartSneakingNode)
                slot(14, OnPlayerStopSneakingNode)
                slot(15, OnPlayerStartSprintingNode)
                slot(16, OnPlayerStopSprintingNode)
                slot(17, OnPlayerSwapHandItemsNode)
                slot(18, OnPlayerChangeSlotNode)
                slot(19, OnPlayerOtherInteraction)
            }
            slot(2, "Numbers", NamedTextColor.RED, Material.SLIME_BALL, InventoryType.CHEST_3_ROW) {
                slot(0, AddNumbersNode)
                slot(1, SubtractNumbersNode)
                slot(2, MultiplyNumbersNode)
                slot(3, DivideNumbersNode)
                slot(4, ModuloNumbersNode)
                slot(5, PowerNumbersNode)
                slot(6, RandomNumberNode)
            }
            slot(4, "Conditions", NamedTextColor.LIGHT_PURPLE, Material.OBSERVER, InventoryType.CHEST_3_ROW) {
                slot(0, IfNode)
                slot(1, GreaterThanNode)
                slot(2, EqualNode)
            }
            slot(6, "Players", NamedTextColor.GOLD, Material.PLAYER_HEAD, InventoryType.CHEST_5_ROW) {
                slot(0, SendMessageNode)
                slot(1, PlayerPositionNode)
                slot(2, KillPlayerNode)
                slot(3, GetPlayerExperience)
                slot(4, SetPlayerExperience)
                slot(5, GetPlayerExpLevel)
                slot(6, SetPlayerExpLevel)
                slot(7, GetPlayerEyeHeight)
                slot(8, GetPlayerFireTicks)
                slot(9, SetPlayerFireTicks)
                slot(10, GetPlayerFlyingSpeed)
                slot(11, SetPlayerFlyingSpeed)
                slot(12, GetPlayerFood)
                slot(13, SetPlayerFood)
                slot(14, GetPlayerSaturation)
                slot(15, SetPlayerSaturation)
                slot(16, GetPlayerHealth)
                slot(17, SetPlayerHealth)
                slot(18, GetPlayerHeldSlot)
                slot(19, SetPlayerHeldSlot)
                slot(20, GetPlayerLatency)
                slot(21, GetPlayerVelocity)
                slot(22, SetPlayerVelocity)
                slot(23, KnockbackPlayer)
                slot(24, PlayerGetStuckArrows)
                slot(25, PlayerSetStuckArrows)
                slot(26, PlayerResetTitle)
                slot(27, PlayerGetAdditionalHearts)
                slot(28, PlayerSetAdditionalHearts)
                slot(29, SetPlayerRespawnPos)
                slot(30, SwingPlayerMainHand)
                slot(31, SwingPlayerOffHand)
                slot(32, GetPlayerName)
                slot(33, SpacePlayersNode)
                slot(34, SetPlayerCanFlyNode)
                slot(35, GetGameModeNode)
                slot(36, SetGameModeNode)
            }
            slot(8, "Flow", NamedTextColor.BLUE, Material.WATER_BUCKET, InventoryType.CHEST_3_ROW) {
                slot(0, ScheduleNode)
                slot(1, ForEachNode)
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
            slot(26, "Vectors", VectorType.color, Material.PRISMARINE_SHARD, InventoryType.CHEST_3_ROW) {
                slot(0, PackVectorNode)
                slot(1, UnpackVectorNode)
            }
            slot(36, "Positions", NamedTextColor.YELLOW, PositionType.material, InventoryType.CHEST_3_ROW) {
                slot(0, PackPositionNode)
                slot(1, UnpackPositionNode)
            }
            slot(38, "World", NamedTextColor.GREEN, Material.GRASS_BLOCK, InventoryType.CHEST_3_ROW) {
                slot(0, GetBlockNode)
                slot(1, SetBlockNode)
                slot(2, FillBlocksNode)
            }
            slot(40, "Variables", NamedTextColor.LIGHT_PURPLE, Material.ANVIL, InventoryType.CHEST_4_ROW) {
                slot(10, VariableNodes.getLocal)
                slot(13, VariableNodes.getSpace)
                slot(16, VariableNodes.getPersistent)
                slot(19, VariableNodes.setLocal)
                slot(22, VariableNodes.setSpace)
                slot(25, VariableNodes.setPersistent)
            }
            slot(42, "Lists", NamedTextColor.WHITE, Material.SHULKER_BOX, InventoryType.CHEST_3_ROW) {
                slot(0, EmptyListNode)
                slot(1, ListAppendNode)
                slot(2, ListGetNode)
                slot(3, ListSetNode)
                slot(4, ListLengthNode)
                slot(5, ListInsertNode)
                slot(6, ListRemoveNode)
            }
            slot(44, "Dictionaries", DictionaryType.color, Material.COBWEB, InventoryType.CHEST_3_ROW) {
                slot(0, EmptyDictionaryNode)
                slot(1, DictionaryGetNode)
                slot(2, DictionarySetNode)
                slot(3, DictionarySizeNode)
                slot(4, DictionaryRemoveNode)
                slot(5, DictionaryKeysNode)
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