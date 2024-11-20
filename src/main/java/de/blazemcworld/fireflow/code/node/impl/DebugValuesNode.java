package de.blazemcworld.fireflow.code.node.impl;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.ConditionType;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.TextType;
import net.kyori.adventure.text.Component;

public class DebugValuesNode extends Node {

    public DebugValuesNode() {
        super("debug_values");

        Output<Double> constant1 = new Output<>("constant1", NumberType.INSTANCE);
        Output<Double> constant2 = new Output<>("constant2", NumberType.INSTANCE);
        Output<Double> constant3 = new Output<>("constant3", NumberType.INSTANCE);
        Output<Component> testText = new Output<>("test_text", TextType.INSTANCE);
        Output<Component> testText2 = new Output<>("test_text2", TextType.INSTANCE);
        Output<Boolean> conditionTrue = new Output<>("condition_true", ConditionType.INSTANCE);
        Output<Boolean> conditionFalse = new Output<>("condition_false", ConditionType.INSTANCE);

        constant1.valueFrom((ctx) -> 1.0);
        constant2.valueFrom((ctx) -> 2.0);
        constant3.valueFrom((ctx) -> 3.0);
        testText.valueFrom((ctx) -> Component.text("TestText!"));
        testText2.valueFrom((ctx) -> Component.text("Test Text TWO!"));
        conditionTrue.valueFrom((ctx) -> true);
        conditionFalse.valueFrom((ctx) -> false);
    }

    @Override
    public Node copy() {
        return new DebugValuesNode();
    }
}
