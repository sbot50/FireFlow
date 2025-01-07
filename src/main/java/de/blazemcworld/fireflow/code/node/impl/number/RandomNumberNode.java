package de.blazemcworld.fireflow.code.node.impl.number;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.StringType;
import net.minestom.server.item.Material;

public class RandomNumberNode extends Node {
    public RandomNumberNode() {
        super("random_number", Material.MULE_SPAWN_EGG);
        Input<String> mode = new Input<>("mode", StringType.INSTANCE).options("Decimal", "Whole");
        Input<Double> min = new Input<>("min", NumberType.INSTANCE);
        Input<Double> max = new Input<>("max", NumberType.INSTANCE);
        Output<Double> output = new Output<>("output", NumberType.INSTANCE);

        output.valueFrom((ctx -> {
            double outputMin = min.getValue(ctx);
            double outputMax = max.getValue(ctx);
            return switch (mode.getValue(ctx)) {
                case "Decimal" -> ((Math.random() * (outputMax - outputMin)) + outputMin);
                case "Whole" -> Math.round(Math.random() * (outputMax - outputMin)) + outputMin;
                default -> 0.0;
            };
        }));
    }

    @Override
    public Node copy() {
        return new RandomNumberNode();
    }
}