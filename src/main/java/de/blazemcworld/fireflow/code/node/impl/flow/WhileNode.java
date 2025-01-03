package de.blazemcworld.fireflow.code.node.impl.flow;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.ConditionType;
import de.blazemcworld.fireflow.code.type.SignalType;
import net.minestom.server.item.Material;

public class WhileNode extends Node {

    public WhileNode() {
        super("while", Material.DAYLIGHT_DETECTOR);

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<Boolean> condition = new Input<>("condition", ConditionType.INSTANCE);
        Output<Void> repeat = new Output<>("repeat", SignalType.INSTANCE);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);

        signal.onSignal((ctx) -> {
            Runnable[] step = { null };
            step[0] = () -> {
                if (condition.getValue(ctx)) {
                    ctx.submit(step[0]);
                    ctx.sendSignal(repeat);
                    return;
                }
                ctx.sendSignal(next);
            };

            step[0].run();

            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new WhileNode();
    }

}