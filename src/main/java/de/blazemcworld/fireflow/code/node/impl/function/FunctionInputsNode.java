package de.blazemcworld.fireflow.code.node.impl.function;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.WireType;

public class FunctionInputsNode extends Node {
    
    public final FunctionDefinition function;

    public FunctionInputsNode(FunctionDefinition function) {
        super("function_inputs");
        this.function = function;
    }

    @SuppressWarnings("unchecked")
    public void addInput(String name, WireType<?> type) {
        Output<?> input = new Output<>(name, type);
        if (type != SignalType.INSTANCE) {
            ((Node.Output<Object>) input).valueFrom((ctx) -> {
                if (ctx.functionStack.isEmpty()) return type.defaultValue();
                FunctionCallNode call = ctx.functionStack.peek();
                if (call.function != function) return type.defaultValue();
                return call.getInput(name).getValue(ctx);
            });
        }
    }

    @Override
    public String getTitle() {
        return function.name + " Inputs";
    }

    @Override
    public Node copy() {
        return new FunctionInputsNode(function);
    }

}
