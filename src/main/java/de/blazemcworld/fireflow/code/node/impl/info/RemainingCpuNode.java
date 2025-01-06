package de.blazemcworld.fireflow.code.node.impl.info;

import de.blazemcworld.fireflow.code.node.Node;
import net.minestom.server.item.Material;
import de.blazemcworld.fireflow.code.type.NumberType;

public class RemainingCpuNode extends Node {
    public RemainingCpuNode() {
        super("remaining_cpu", Material.RED_DYE);

        Output<Double> nanoseconds = new Output<>("nanoseconds", NumberType.INSTANCE);

        nanoseconds.valueFrom(ctx -> (double) ctx.evaluator.remainingCpu());
    }

    @Override
    public Node copy() {
        return new RemainingCpuNode();
    }
}