package de.blazemcworld.fireflow.inventory

import de.blazemcworld.fireflow.database.DatabaseHelper
import de.blazemcworld.fireflow.preferences.ReloadPreference
import net.kyori.adventure.text.format.NamedTextColor
import net.minestom.server.entity.Player
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
            item.setTag(Tag.Byte("value"), value)
            inv.setItemStack(slot, item.build())
            slot++
        }

        inv.addInventoryCondition click@{ who, slot, type, result ->
            if (player != who) return@click
            val item = inv.getItemStack(slot)
            if (!item.hasTag(Tag.String("preference"))) return@click

            val key = item.getTag(Tag.String("preference"))
            if (!preferences.containsKey(key)) return@click
            knownPreferences[key] = preferences[key]!!.increaseState(item.getTag(Tag.Byte("value")))

            val value = knownPreferences[key]
            val lore = preferences[key]!!.getLore()
            lore[value!!.toInt()] = lore[value.toInt()].color(NamedTextColor.YELLOW)
            val newItem = ItemStack
                .builder(preferences[key]!!.getState(value).getIcon())
                .customName(preferences[key]!!.getName())
                .lore(lore)
            newItem.setTag(Tag.String("preference"), key)
            newItem.setTag(Tag.Byte("value"), value)
            inv.setItemStack(slot, newItem.build())
            return@click
        }

        player.openInventory(inv)
    }

    fun close(player: Player, inv: Inventory) {
        val knownPreferences = mutableMapOf<String, Byte>()
        for (item in inv.itemStacks) {
            if (!item.hasTag(Tag.String("preference"))) continue
            val key = item.getTag(Tag.String("preference"))
            val value = item.getTag(Tag.Byte("value"))
            knownPreferences[key] = value
        }

        DatabaseHelper.updatePreferences(player, knownPreferences)
    }

}