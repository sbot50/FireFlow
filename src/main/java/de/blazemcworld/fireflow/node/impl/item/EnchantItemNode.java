package de.blazemcworld.fireflow.node.impl.item;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.EnchantmentValue;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.NumberValue;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.EnchantmentList;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.registry.DynamicRegistry;

public class EnchantItemNode extends Node {
    public EnchantItemNode() {
        super("Enchant Item");

        input("Item", ItemValue.INSTANCE);
        input("Enchant", EnchantmentValue.INSTANCE);
        input("Level", NumberValue.INSTANCE);
        output("Result", ItemValue.INSTANCE);

        loadJava(EnchantItemNode.class);
    }

    @FlowValueOutput("Result")
    private static ItemStack result() {
        ItemStack item = item();
        EnchantmentList list = item.get(ItemComponent.ENCHANTMENTS);
        list = list.with(enchant(), (int) level());
        return item.with(ItemComponent.ENCHANTMENTS, list);
    }

    @FlowValueInput("Item")
    private static ItemStack item()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Enchant")
    private static DynamicRegistry.Key<Enchantment> enchant()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Level")
    private static double level()  {
        throw new IllegalStateException();
    }
}
