package de.blazemcworld.fireflow.code.node.impl.number;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.TextType;
import net.kyori.adventure.text.Component;

public class NumberToTextNode extends Node {

    public NumberToTextNode() {
        super("number_to_text");

        Input<Double> number = new Input<>("number", NumberType.INSTANCE);
        Output<Component> text = new Output<>("text", TextType.INSTANCE);

        text.valueFrom((ctx) -> Component.text(number.getValue(ctx)));
    }

    @Override
    public Node copy() {
        return new NumberToTextNode();
    }
}
