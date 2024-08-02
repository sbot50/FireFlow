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
import net.minestom.server.item.ItemStack
import net.minestom.server.tag.Tag

object PreferencesInventory {

    private val preferences = mapOf(
        "reload" to ReloadPreference
    )

    fun open(player: Player) {
        val inv = Inventory(InventoryType.CHEST_1_ROW,"Preferences")
        val knownPreferences = DatabaseHelper.preferences(player)

        var slot = 0
        for ((key, value) in knownPreferences) {
            if (!preferences.containsKey(key)) continue
            val lore = preferences[key]!!.getLore()
            lore[value.toInt()] = lore[value.toInt()].color(NamedTextColor.YELLOW)
            val item = ItemStack
                .builder(preferences[key]!!.getState(value).getIcon())
                .customName(preferences[key]!!.getName())
                .lore(lore)
            item.setTag(Tag.String("preference"), key)
            inv.setItemStack(slot, item.build())
            slot++
        }

        inv.addInventoryCondition click@{ who, slot, type, result ->
            if (player != who) return@click
            val item = inv.getItemStack(slot)
            if (!item.hasTag(Tag.String("preference"))) return@click
            val key = item.getTag(Tag.String("preference"))
            knownPreferences[key] = preferences[key]!!.increaseState(knownPreferences[key]!!)

            val value = knownPreferences[key]
            val lore = preferences[key]!!.getLore()
            lore[value!!.toInt()] = lore[value.toInt()].color(NamedTextColor.YELLOW)
            val newItem = ItemStack
                .builder(preferences[key]!!.getState(value).getIcon())
                .customName(preferences[key]!!.getName())
                .lore(lore)
            newItem.setTag(Tag.String("preference"), key)
            inv.setItemStack(slot, newItem.build())
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

}