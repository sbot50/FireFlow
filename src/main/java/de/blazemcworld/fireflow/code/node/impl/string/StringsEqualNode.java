package de.blazemcworld.fireflow.code.node.impl.string;

import de.blazemcworld.fireflow.code.node.Node;
import net.minestom.server.item.Material;
import de.blazemcworld.fireflow.code.type.StringType;
import de.blazemcworld.fireflow.code.type.ConditionType;

public class StringsEqualNode extends Node {
    public StringsEqualNode() {
        super("strings_equal", Material.COMPARATOR);

        Input<String> primary = new Input<>("primary", StringType.INSTANCE);
        Varargs<String> others = new Varargs<>("others", StringType.INSTANCE);
        Output<Boolean> equal = new Output<>("equal", ConditionType.INSTANCE);

        equal.valueFrom((ctx) -> {
            String v = primary.getValue(ctx);
            for (String other : others.getVarargs(ctx)) {
                if (!other.equals(v)) return false;
            }
            return true;
        });
    }

    @Override
    public Node copy() {
        return new StringsEqualNode();
    }
}