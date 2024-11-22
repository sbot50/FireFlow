package de.blazemcworld.fireflow.code.node.impl.number;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import net.minestom.server.item.Material;

public class MultiplyNumbersNode extends Node {
    
    public MultiplyNumbersNode() {
        super("multiply_numbers", Material.SMITHING_TABLE);
        
        Input<Double> left = new Input<>("left", NumberType.INSTANCE);
        Input<Double> right = new Input<>("right", NumberType.INSTANCE);
        Output<Double> result = new Output<>("result", NumberType.INSTANCE);
        
        result.valueFrom((ctx) -> left.getValue(ctx) * right.getValue(ctx));
    }

    @Override
    public Node copy() {
        return new MultiplyNumbersNode();
    }

}
