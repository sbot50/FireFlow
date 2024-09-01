package de.blazemcworld.fireflow.node.impl.extraction.enchant;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.util.TextCase;
import de.blazemcworld.fireflow.value.EnchantmentValue;
import de.blazemcworld.fireflow.value.TextValue;
import net.minestom.server.item.enchant.Enchantment;
import net.minestom.server.registry.DynamicRegistry;

public class EnchantName extends ExtractionNode {

    public EnchantName() {
        super("Enchantment Name", EnchantmentValue.INSTANCE, TextValue.INSTANCE);

        loadJava(EnchantName.class);
    }

    @FlowValueOutput("")
    private static String output() {
        return TextCase.namespaceToName(input().namespace());
    }

    @FlowValueInput("")
    private static DynamicRegistry.Key<Enchantment> input()  {
        throw new IllegalStateException();
    }

}
