package de.blazemcworld.fireflow.node.impl.extraction.text;

import de.blazemcworld.fireflow.node.ExtractionNode;
import de.blazemcworld.fireflow.node.annotation.FlowValueInput;
import de.blazemcworld.fireflow.node.annotation.FlowValueOutput;
import de.blazemcworld.fireflow.value.MessageValue;
import de.blazemcworld.fireflow.value.TextValue;
import net.kyori.adventure.text.Component;

public class TextToMessageNode extends ExtractionNode {
    public TextToMessageNode() {
        super("Text to Message", TextValue.INSTANCE, MessageValue.INSTANCE);

        loadJava(TextToMessageNode.class);
    }

    @FlowValueOutput("")
    private static Component output() {
        return Component.text(input());
    }

    @FlowValueInput("")
    private static String input() {
        throw new IllegalStateException();
    }
}
