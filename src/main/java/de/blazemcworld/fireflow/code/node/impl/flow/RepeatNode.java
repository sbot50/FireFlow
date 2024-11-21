package de.blazemcworld.fireflow.code.node.impl.flow;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.SignalType;

public class RepeatNode extends Node {
    
    public RepeatNode() {
        super("repeat");

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<Double> times = new Input<>("times", NumberType.INSTANCE);
        Output<Void> repeat = new Output<>("repeat", SignalType.INSTANCE);
        Output<Double> index = new Output<>("index", NumberType.INSTANCE);
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);
        index.valueFromThread();

        signal.onSignal((ctx) -> {
            double total = times.getValue(ctx);
            for (int i = 0; i < total; i++) {
                if (ctx.timelimitHit()) return;
                ctx.setThreadValue(index, i + 1.0);
                repeat.sendSignalImmediately(ctx);
            }
            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new RepeatNode();
    }

}
