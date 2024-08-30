package de.blazemcworld.fireflow.node.impl.item;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.EnchantmentValue;
import de.blazemcworld.fireflow.value.ItemValue;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.component.EnchantmentList;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.registry.DynamicRegistry;

public class RemoveEnchantItemNode extends Node {
    public RemoveEnchantItemNode() {
        super("Remove Enchant");

        input("Item", ItemValue.INSTANCE);
        input("Enchant", EnchantmentValue.INSTANCE);
        output("Result", ItemValue.INSTANCE);

        loadJava(RemoveEnchantItemNode.class);
    }

    @FlowValueOutput("Result")
    private static ItemStack result() {
        ItemStack item = item();
        EnchantmentList list = item.get(ItemComponent.ENCHANTMENTS);
        list = list.remove(enchant());
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
}
