package de.blazemcworld.fireflow.code.node.impl.variable;

import de.blazemcworld.fireflow.code.VariableStore;
import de.blazemcworld.fireflow.code.node.Node;
import de.blazemcworld.fireflow.code.type.NumberType;
import de.blazemcworld.fireflow.code.type.SignalType;
import de.blazemcworld.fireflow.code.type.StringType;
import net.minestom.server.item.Material;

public class IncrementVariableNode extends Node {
    public IncrementVariableNode() {
        super("increment_variable", Material.NETHERITE_SCRAP);

        Input<Void> signal = new Input<>("signal", SignalType.INSTANCE);
        Input<String> name = new Input<>("name", StringType.INSTANCE);
        Input<String> scope = new Input<>("scope", StringType.INSTANCE)
                .options("thread", "session", "saved");
        Output<Void> next = new Output<>("next", SignalType.INSTANCE);

        signal.onSignal((ctx) -> {
            VariableStore store = switch (scope.getValue(ctx)) {
                case "saved" -> ctx.evaluator.space.savedVariables;
                case "session" -> ctx.evaluator.sessionVariables;
                case "thread" -> ctx.threadVariables;
                default -> null;
            };
            if (store != null) {
                String id = name.getValue(ctx);
                store.set(id, NumberType.INSTANCE, store.get(id, NumberType.INSTANCE) + 1);
            }
            ctx.sendSignal(next);
        });
    }

    @Override
    public Node copy() {
        return new IncrementVariableNode();
    }
}