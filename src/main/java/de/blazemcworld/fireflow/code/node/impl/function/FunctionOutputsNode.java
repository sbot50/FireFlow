package de.blazemcworld.fireflow.code.node.impl.function;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.WireType;

public class FunctionOutputsNode extends Node {
    
    public FunctionDefinition function;

    public FunctionOutputsNode(FunctionDefinition function) {
        super("function_outputs");
        this.function = function;
    }

    public void addOutput(String name, WireType<?> type) {
        Input<?> output = new Input<>(name, type);
        if (type == SignalType.INSTANCE) {
            output.onSignal((ctx) -> {
                if (ctx.functionStack.isEmpty()) {
                    for (FunctionCallNode call : function.callNodes) {
                        call.getOutput(name).sendSignalImmediately(ctx);
                    }
                    return;
                }
                FunctionCallNode call = ctx.functionStack.peek();
                if (call.function != function) return;
                ctx.functionStack.pop();
                call.getOutput(name).sendSignalImmediately(ctx);
                ctx.functionStack.push(call);
                ctx.clearQueue();
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
