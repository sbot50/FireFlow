package de.blazemcworld.fireflow.code.node.impl.function;

import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.SignalType;

public class FunctionCallNode extends Node {

    public final FunctionDefinition function;

    @SuppressWarnings("unchecked")
    public FunctionCallNode(FunctionDefinition function) {
        super("function_call");
        this.function = function;

        for (Node.Output<?> matching : function.inputsNode.outputs) {
            Input<?> input = new Input<>(matching.id, matching.type);
            if (input.type == SignalType.INSTANCE) {
                input.onSignal((ctx) -> {
                    ctx.functionStack.push(this);
                    matching.sendSignalImmediately(ctx);
                    ctx.clearQueue();
                    ctx.functionStack.pop();
                });
            }
        }
        for (Node.Input<?> matching : function.outputsNode.inputs) {
            Output<?> output = new Output<>(matching.id, matching.type);
            if (output.type != SignalType.INSTANCE) {
                ((Node.Output<Object>) output).valueFrom((ctx) -> {
                    ctx.functionStack.push(this);
                    Object out = matching.getValue(ctx);
                    ctx.clearQueue();
                    ctx.functionStack.pop();
                    return out;
                });
            }
        }
        function.callNodes.add(this);
    }

    @Override
    public String getTitle() {
        return function.name;
    }

    @Override
    public Node copy() {
        return new FunctionCallNode(function);
    }

    public Node.Input<?> getInput(String name) {
        for (Node.Input<?> input : inputs) {
            if (input.id.equals(name)) {
                return input;
            }
        }
        return null;
    }

    public Node.Output<?> getOutput(String name) {
        for (Node.Output<?> output : outputs) {
            if (output.id.equals(name)) {
                return output;
            }
        }
        return null;
    }

}
