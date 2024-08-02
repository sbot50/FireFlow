package de.blazemcworld.fireflow.inventory

import de.blazemcworld.fireflow.database.DatabaseHelper
import de.blazemcworld.fireflow.preferences.ReloadPreference
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.MinecraftServer
import net.minestom.server.entity.Player
import net.minestom.server.event.EventFilter
import net.minestom.server.event.EventNode
import net.minestom.server.event.inventory.InventoryCloseEvent
import net.minestom.server.inventory.Inventory
import net.minestom.server.inventory.InventoryType
import net.minestom.server.inventory.click.ClickType
import net.minestom.server.item.ItemStack

object PreferencesInventory {

    private val preferences = mapOf(
        "reload" to ReloadPreference
    )

    fun open(player: Player) {
        val preferenceItemMap = mutableMapOf<Int, String>()

        val inv = Inventory(InventoryType.CHEST_1_ROW,"Preferences")
        val knownPreferences = DatabaseHelper.preferences(player)

        var slot = 0
        for ((key, value) in knownPreferences) {
            if (!preferences.containsKey(key)) continue
            inv.setItemStack(slot, createItem(key, value))
            preferenceItemMap[slot] = key
            slot++
        }

        inv.addInventoryCondition click@{ who, slot, type, result ->
            if (player != who || slot !in preferenceItemMap) return@click

            val key = preferenceItemMap[slot]!!
            if (!knownPreferences.containsKey(key)) return@click
            if (type == ClickType.RIGHT_CLICK) knownPreferences[key] = preferences[key]!!.decreaseState(knownPreferences[key]!!)
            else knownPreferences[key] = preferences[key]!!.increaseState(knownPreferences[key]!!)

            inv.setItemStack(slot, createItem(key, knownPreferences[key]!!))
            return@click
        }

        player.openInventory(inv)

        val handler = MinecraftServer.getGlobalEventHandler()
        val node = EventNode.type("closeinv", EventFilter.INVENTORY)

        node.addListener(InventoryCloseEvent::class.java) {
            if (it.inventory != inv) return@addListener
            DatabaseHelper.updatePreferences(player, knownPreferences)
            handler.removeChild(node)
        }

        handler.addChild(node)
    }

    private fun createItem(preference: String, value: Byte): ItemStack {
        val lore = preferences[preference]!!.getLore()
        lore[value.toInt()] = lore[value.toInt()].color(NamedTextColor.YELLOW)

        return ItemStack
            .builder(preferences[preference]!!.getState(value).getIcon())
            .customName(preferences[preference]!!.getName())
            .lore(lore)
            .build()
    }

}