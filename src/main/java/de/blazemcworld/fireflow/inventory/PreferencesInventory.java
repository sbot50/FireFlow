package de.blazemcworld.fireflow.inventory;

import de.blazemcworld.fireflow.preferences.PlayerIndex;
import de.blazemcworld.fireflow.preferences.Preference;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;

import java.util.List;
import java.util.Map;

public class PreferencesInventory {

    private static ItemStack createItem(Preference pref, int value) {
        List<Component> lore = pref.getLore();
        lore.set(value, lore.get(value).color(NamedTextColor.YELLOW));

        return ItemStack.builder(pref.states[value].icon())
                .customName(pref.getDesc())
                .lore(lore)
                .build();
    }

    public static void open(Player player) {
        Map<Preference, Integer> preferences = PlayerIndex.get(player).preferences;

        Inventory inv = new Inventory(InventoryType.CHEST_1_ROW, "Preferences");

        int index = 0;
        for (Preference pref : Preference.values()) {
            inv.setItemStack(index, createItem(pref, preferences.getOrDefault(pref, 0)));
            index++;
        }

        inv.addInventoryCondition((who, slot, type, result) -> {
            result.setCancel(true);
            if (player != who || Preference.values().length <= slot) return;

            Preference pref = Preference.values()[slot];
            int state = preferences.getOrDefault(pref, 0);
            state = Math.floorMod(state + (type == ClickType.LEFT_CLICK ? 1 : -1), pref.states.length);
            preferences.put(pref, state);

            inv.setItemStack(slot, createItem(pref, state));
        });

        player.openInventory(inv);
    }
}