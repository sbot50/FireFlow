package de.blazemcworld.fireflow.node.impl.item;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.DictionaryValue;
import de.blazemcworld.fireflow.value.EnchantmentValue;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.NumberValue;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.EnchantmentList;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.registry.DynamicRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class SetItemEnchantsNode extends Node {
    public SetItemEnchantsNode() {
        super("Set Item Enchants");

        input("Item", ItemValue.INSTANCE);
        input("Enchants", DictionaryValue.get(EnchantmentValue.INSTANCE, NumberValue.INSTANCE));
        output("Result", ItemValue.INSTANCE);

        loadJava(SetItemEnchantsNode.class);
    }

    @FlowValueOutput("Result")
    private static ItemStack result() {
        Map<DynamicRegistry.Key<Enchantment>, Integer> enchants = new HashMap<>();
        for (Map.Entry<DynamicRegistry. Key<Enchantment>, Double> entry : enchants().entrySet()) {
            enchants.put(entry.getKey(), entry.getValue().intValue());
        }
        return item().with(ItemComponent.ENCHANTMENTS, new EnchantmentList(enchants));
    }

    @FlowValueInput("Item")
    private static ItemStack item()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Enchants")
    private static @NotNull Map<DynamicRegistry.Key<Enchantment>, Double> enchants()  {
        throw new IllegalStateException();
    }
}
