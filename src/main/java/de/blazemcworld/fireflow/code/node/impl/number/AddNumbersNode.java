package de.blazemcworld.fireflow.code.node.impl.number;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;

public class AddNumbersNode extends Node {

    public AddNumbersNode() {
        super("add_numbers");

        Input<Double> left = new Input<>("left", NumberType.INSTANCE);
        Input<Double> right = new Input<>("right", NumberType.INSTANCE);
        Output<Double> result = new Output<>("result", NumberType.INSTANCE);

        result.valueFrom((ctx) -> left.getValue(ctx) + right.getValue(ctx));
    }

    @Override
    public Node copy() {
        return new AddNumbersNode();
    }
}
