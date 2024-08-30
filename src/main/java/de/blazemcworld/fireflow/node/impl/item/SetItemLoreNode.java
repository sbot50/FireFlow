package de.blazemcworld.fireflow.node.impl.item;

import de.blazemcworld.fireflow.node.Node;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.ItemValue;
import de.blazemcworld.fireflow.value.ListValue;
import de.blazemcworld.fireflow.value.MessageValue;
import net.kyori.adventure.text.Component;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SetItemLoreNode extends Node {
    public SetItemLoreNode() {
        super("Set Item Lore");

        input("Item", ItemValue.INSTANCE);
        input("Lore", ListValue.get(MessageValue.INSTANCE));
        output("Result", ItemValue.INSTANCE);

        loadJava(SetItemLoreNode.class);
    }

    @FlowValueOutput("Result")
    private static ItemStack result() {
        return item().withLore(lore());
    }

    @FlowValueInput("Item")
    private static ItemStack item()  {
        throw new IllegalStateException();
    }

    @FlowValueInput("Lore")
    private static @NotNull List<Component> lore()  {
        throw new IllegalStateException();
    }
}
