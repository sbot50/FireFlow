package de.blazemcworld.fireflow.inventory;

import de.blazemcworld.fireflow.preferences.PlayerIndex;
import de.blazemcworld.fireflow.preferences.PlayerInfo;
import de.blazemcworld.fireflow.preferences.Preference;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.inventory.click.ClickType;
import net.minestom.server.item.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PreferencesInventory {

    private static ItemStack createItem(String preference, int value) {
        Preference pref = PlayerInfo.preferenceTranslator.get(preference);

        List<Component> lore = pref.getLore();
        lore.set(value, lore.get(value).color(NamedTextColor.YELLOW));

        return ItemStack
                .builder(pref.getState(value).icon())
                .customName(pref.getDesc())
                .lore(lore)
                .build();
    }

    public static void open(Player player) {
        List<String> preferenceItemMap = new ArrayList<>();
        Map<String, Integer> preferences = PlayerIndex.get(player).preferences;

        Inventory inv = new Inventory(InventoryType.CHEST_1_ROW, "Preferences");

        var index = 0;
        for (Map.Entry<String, Integer> entry : preferences.entrySet()) {
            inv.setItemStack(index, createItem(entry.getKey(), entry.getValue()));
            preferenceItemMap.add(entry.getKey());
            index++;
        }

        inv.addInventoryCondition((who, slot, type, ignore)-> {
            if (player != who || preferenceItemMap.size() <= slot) return;

            Preference pref = PlayerInfo.preferenceTranslator.get(preferenceItemMap.get(slot));
            int state;
            if (type == ClickType.LEFT_CLICK) state = pref.increaseState(PlayerIndex.get(player).preferences.get(pref.getName()));
            else state = pref.decreaseState(PlayerIndex.get(player).preferences.get(pref.getName()));
            preferences.put(pref.getName(), state);

            inv.setItemStack(slot, createItem(pref.getName(), state));
        });

        player.openInventory(inv);
    }
}