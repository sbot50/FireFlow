package de.blazemcworld.fireflow.code.node.impl.flow;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.ConditionType;
import net.minestom.server.item.Material;

public class InvertConditionNode extends Node {

    public InvertConditionNode() {
        super("invert_condition", Material.REDSTONE_TORCH);

        Input<Boolean> normal = new Input<>("normal", ConditionType.INSTANCE);
        Output<Boolean> inverted = new Output<>("inverted", ConditionType.INSTANCE);

        inverted.valueFrom((ctx) -> !normal.getValue(ctx));
    }

    @Override
    public Node copy() {
        return new InvertConditionNode();
    }
}
