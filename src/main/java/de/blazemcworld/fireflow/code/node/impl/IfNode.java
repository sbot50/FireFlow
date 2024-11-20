package de.blazemcworld.fireflow.code.node.impl;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.ConditionType;
import de.blazemcworld.fireflow.code.type.SignalType;

public class IfNode extends Node {

    public IfNode() {
        super("if");

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<Boolean> condition = new Input<>("condition", ConditionType.INSTANCE);

        Output<Void> trueOut = new Output<>("true", SignalType.INSTANCE);
        Output<Void> falseOut = new Output<>("false", SignalType.INSTANCE);

        signal.onSignal((ctx) -> {
            if (condition.getValue(ctx)) {
                ctx.sendSignal(trueOut);
            } else {
                ctx.sendSignal(falseOut);
            }
        });
    }

    @Override
    public Node copy() {
        return new IfNode();
    }
}

