package de.blazemcworld.fireflow.code.node.impl.number;

import de.blazemcworld.fireflow.code.node.Node;
import net.minestom.server.item.Material;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.ConditionType;

public class NumbersEqualNode extends Node {
    public NumbersEqualNode() {
        super("numbers_equal", Material.COMPARATOR);

        Input<Double> primary = new Input<>("primary", NumberType.INSTANCE);
        Varargs<Double> others = new Varargs<>("others", NumberType.INSTANCE);
        Output<Boolean> equal = new Output<>("equal", ConditionType.INSTANCE);

        equal.valueFrom((ctx) -> {
            double v = primary.getValue(ctx);
            for (double other : others.getVarargs(ctx)) {
                if (other != v) return false;
            }
            return true;
        });
    }

    @Override
    public Node copy() {
        return new NumbersEqualNode();
    }
}