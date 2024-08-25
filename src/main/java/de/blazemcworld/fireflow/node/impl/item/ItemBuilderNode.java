package de.blazemcworld.fireflow.node.impl.item;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.*;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemBuilderNode extends Node {
    public ItemBuilderNode() {
        super("Item Builder");

        input("Material", MaterialValue.INSTANCE);
        input("Count", NumberValue.INSTANCE);
        input("Lore", ListValue.get(MessageValue.INSTANCE));
//        input("Enchants", DictionaryValue.get(EnchantmentValue.INSTANCE, NumberValue.INSTANCE));
        output("Result", ItemValue.INSTANCE);

        loadJava(ItemBuilderNode.class);
    }

    @FlowValueOutput("Result")
    private static ItemStack item()  {
        return ItemStack.builder(material())
                .amount((int) count())
                .lore(lore())
//                .set(ItemComponent.ENCHANTMENTS, new EnchantmentList(enchants()))
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

    @FlowValueInput("Lore")
    private static @NotNull List<Component> lore()  {
        throw new IllegalStateException();
    }

//    @FlowValueInput("Enchants")
//    private static Dictionary<Enchantment, Double> enchants()  {
//        throw new IllegalStateException();
//    }
}
