package de.blazemcworld.fireflow.code.node.impl.number;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import net.minestom.server.item.Material;

public class SquareRootNode extends Node {

    public SquareRootNode() {
        super("square_root", Material.BEETROOT);

        Input<Double> number = new Input<>("number", NumberType.INSTANCE);
        Output<Double> result = new Output<>("result", NumberType.INSTANCE);

        result.valueFrom((ctx) -> {
            double out = number.getValue(ctx);
            return Math.sqrt(out);
        });
    }

    @Override
    public Node copy() {
        return new SquareRootNode();
    }

}
