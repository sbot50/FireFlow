package de.blazemcworld.fireflow.code.node.impl.flow;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.SignalType;
import net.minestom.server.item.Material;

public class RepeatNode extends Node {
    
    public RepeatNode() {
        super("repeat", Material.REPEATER);

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<Double> times = new Input<>("times", NumberType.INSTANCE);
        Output<Void> repeat = new Output<>("repeat", SignalType.INSTANCE);
        Output<Double> index = new Output<>("index", NumberType.INSTANCE);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);
        index.valueFromThread();

        signal.onSignal((ctx) -> {
            int max = times.getValue(ctx).intValue();
            double[] i = new double[] { 0 };

            Runnable[] step = { null };
            step[0] = () -> {
                if (i[0] >= max) {
                    ctx.sendSignal(next);
                    return;
                }
                ctx.setThreadValue(index, i[0]++);
                ctx.submit(step[0]);
                ctx.sendSignal(repeat);
            };

            step[0].run();
        });
    }

    @Override
    public Node copy() {
        return new RepeatNode();
    }

}
