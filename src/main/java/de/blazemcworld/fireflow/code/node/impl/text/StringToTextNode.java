package de.blazemcworld.fireflow.code.node.impl.text;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.StringType;
import de.blazemcworld.fireflow.code.type.TextType;
import net.kyori.adventure.text.Component;

public class StringToTextNode extends Node {
    
    public StringToTextNode() {
        super("string_to_text");

        Input<String> input = new Input<>("string", StringType.INSTANCE);
        Output<Component> output = new Output<>("text", TextType.INSTANCE);

        output.valueFrom((ctx) -> Component.text(input.getValue(ctx)));
    }

    @Override
    public Node copy() {
        return new StringToTextNode();
    }

}
