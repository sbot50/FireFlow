package de.blazemcworld.fireflow.node.impl.extraction.item;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.DictionaryValue;
import de.blazemcworld.fireflow.value.EnchantmentValue;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.NumberValue;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.registry.DynamicRegistry;

import java.util.HashMap;
import java.util.Map;

public class ItemEnchants extends ExtractionNode {

    public ItemEnchants() {
        super("Item Enchants", ItemValue.INSTANCE, DictionaryValue.get(EnchantmentValue.INSTANCE, NumberValue.INSTANCE));

        loadJava(ItemEnchants.class);
    }

    @FlowValueOutput("")
    private static Map<DynamicRegistry.Key<Enchantment>, Double> output() {
        Map<DynamicRegistry.Key<Enchantment>, Double> enchants = new HashMap<>();
        for (Map.Entry<DynamicRegistry.Key<Enchantment>, Integer> entry : input().get(ItemComponent.ENCHANTMENTS).enchantments().entrySet()) {
            enchants.put(entry.getKey(), entry.getValue().doubleValue());
        }
        return enchants;
    }

    @FlowValueInput("")
    private static ItemStack input()  {
        throw new IllegalStateException();
    }

}
