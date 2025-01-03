package de.blazemcworld.fireflow.code.node.impl.function;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.WireType;

public class FunctionOutputsNode extends Node {
    
    public FunctionDefinition function;

    public FunctionOutputsNode(FunctionDefinition function) {
        super("function_outputs", function.icon);
        this.function = function;
    }

    @SuppressWarnings("unchecked")
    public void addOutput(String name, WireType<?> type) {
        Input<?> output = new Input<>(name, type);
        if (type == SignalType.INSTANCE) {
            output.onSignal((ctx) -> {
                if (ctx.functionStack.isEmpty()) {
                    for (FunctionCallNode call : function.callNodes) {
                        ctx.sendSignal((Output<Void>) call.getOutput(name));
                    }
                    return;
                }
                FunctionCallNode call = ctx.functionStack.peek();
                if (call.function != function) return;
                ctx.functionStack.pop();
                ctx.submit(() -> {
                    ctx.functionStack.push(call);
                    ctx.clearQueue();
                });
                ctx.sendSignal((Output<Void>) call.getOutput(name));
            });
        }
    }

    @Override
    public String getTitle() {
        return function.name + " Outputs";
    }

    @Override
    public Node copy() {
        return new FunctionOutputsNode(function);
    }

}
