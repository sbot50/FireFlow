package de.blazemcworld.fireflow.node.impl.extraction.text;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.MessageValue;
import de.blazemcworld.fireflow.value.TextValue;
import net.kyori.adventure.text.Component;

public class FormatToMessageNode extends ExtractionNode {
    public FormatToMessageNode() {
        super("Format Message", TextValue.INSTANCE, MessageValue.INSTANCE);

        loadJava(FormatToMessageNode.class);
    }

    @FlowValueOutput("")
    private static Component output() {
        return MessageValue.MM.deserialize(input());
    }

    @FlowValueInput("")
    private static String input() {
        throw new IllegalStateException();
    }
}
