package de.blazemcworld.fireflow.node.impl.item;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.*;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.EnchantmentList;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.registry.DynamicRegistry;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemBuilderNode extends Node {
    public ItemBuilderNode() {
        super("Item Builder");

        input("Material", MaterialValue.INSTANCE);
        input("Count", NumberValue.INSTANCE);
        input("Name", MessageValue.INSTANCE);
        input("Lore", ListValue.get(MessageValue.INSTANCE));
        input("Enchants", DictionaryValue.get(EnchantmentValue.INSTANCE, NumberValue.INSTANCE));
        output("Result", ItemValue.INSTANCE);

        loadJava(ItemBuilderNode.class);
    }

    @FlowValueOutput("Result")
    private static ItemStack item()  {
        Map<DynamicRegistry.Key<Enchantment>, Integer> enchants = new HashMap<>();
        for (Map.Entry<DynamicRegistry. Key<Enchantment>, Double> entry : enchants().entrySet()) {
            enchants.put(entry.getKey(), entry.getValue().intValue());
        }

        return ItemStack.builder(material())
                .amount((int) count())
                .customName(name())
                .lore(lore())
                .set(ItemComponent.ENCHANTMENTS, new EnchantmentList(enchants))
                .build();
    }

    @FlowValueInput("Material")
    private static Material material()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Count")
    private static double count()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Name")
    private static @NotNull Component name()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Lore")
    private static @NotNull List<Component> lore()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Enchants")
    private static Map<DynamicRegistry.Key<Enchantment>, Double> enchants()  {
        throw new IllegalStateException();
    }
}
