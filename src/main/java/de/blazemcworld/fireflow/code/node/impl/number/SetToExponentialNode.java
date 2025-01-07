package de.blazemcworld.fireflow.code.node.impl.number;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import net.minestom.server.item.Material;

public class SetToExponentialNode extends Node {
    public SetToExponentialNode() {
        super("set_to_exponential", Material.CHEST);
        Input<Double> base = new Input<>("base", NumberType.INSTANCE);
        Input<Double> exponent = new Input<>("exponent", NumberType.INSTANCE);
        Output<Double> result = new Output<>("result", NumberType.INSTANCE);

        result.valueFrom((ctx -> Math.pow(base.getValue(ctx), exponent.getValue(ctx))));
    }

    @Override
    public Node copy() {
        return new SetToExponentialNode();
    }
}